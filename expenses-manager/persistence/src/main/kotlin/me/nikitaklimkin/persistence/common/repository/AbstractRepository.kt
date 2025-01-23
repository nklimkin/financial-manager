package me.nikitaklimkin.persistence.common.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.persistence.common.model.PersistenceModel
import mu.KotlinLogging
import org.litote.kmongo.*

private val log = KotlinLogging.logger { }

interface AbstractRepository<T : PersistenceModel> {

    val col: MongoCollection<T>

    fun getById(id: Id<T>): T? {
        log.debug { "get by id $id" }
        return col.findOne(PersistenceModel::id eq id)
    }

    fun getAll(): Collection<T>? {
        log.debug { "get all" }
        val res = col.find()
        return res.asIterable().map { it }
    }

    fun delete(id: Id<T>): T? {
        log.debug { "delete entity with id $id" }
        return col.findOneAndDelete(PersistenceModel::id eq id)
    }

    fun deleteAll() {
        log.debug { "delete all" }
        col.deleteMany()
    }

    fun add(entry: T): Either<PersistenceLayerError.IllegalAdd, T> {
        log.debug { "add new entry" }
        log.trace { "new entry is $entry" }
        return try {
            col.insertOne(entry)
            entry.right()
        } catch (writeException: MongoWriteException) {
            val code = writeException.error.code
            if (code == 11000) {
                return PersistenceLayerError.IllegalAdd(writeException.message).left()
            }
            throw writeException;
        }
    }

    fun addAll(entries: Collection<T>): Either<PersistenceLayerError.IllegalAdd, Collection<T>> {
        log.debug { "add ${entries.size} entries" }
        log.trace { "Add $entries" }
        return try {
            col.insertMany(entries.toMutableList())
            entries.right()
        } catch (writeException: MongoWriteException) {
            val code = writeException.error.code
            if (code == 11000) {
                return PersistenceLayerError.IllegalAdd(writeException.message).left()
            }
            throw writeException;
        }
    }

    fun update(entry: T): Either<PersistenceLayerError, T> {
        log.debug { "update entry" }
        val updateResult = col.updateOneById(
            entry.id,
            entry
        )
        if (updateResult.modifiedCount == 0L) {
            return PersistenceLayerError.EntityNotFound(entry.id).left()
        }
        return entry.right()
    }

    fun updateAll(entries: Collection<T>): Either<PersistenceLayerError, Collection<T>> {
        log.debug { "Update ${entries.size} entries" }
        log.trace { "Update $entries" }
        try {
            val (errorUpdatedItems, successUpdatedItems) = updateAndReturnErrorAndSuccessResults(entries)
            return if (errorUpdatedItems.isNotEmpty()) {
                processErrorUpdatedItems(errorUpdatedItems).left()
            } else {
                successUpdatedItems.right()
            }
        } catch (exception: Exception) {
            return PersistenceLayerError.IllegalUpdate(exception.message).left()
        }
    }

    fun updateAndReturnErrorAndSuccessResults(entries: Collection<T>): Pair<MutableList<T>, MutableList<T>> {
        val successfullyUpdatedItems = mutableListOf<T>()
        val errorUpdatedItems = mutableListOf<T>()

        for (entry in entries) {
            update(entry)
                .fold(
                    { _ -> errorUpdatedItems.add(entry) },
                    { right -> successfullyUpdatedItems.add(right) }
                )
        }

        return Pair(errorUpdatedItems, successfullyUpdatedItems)
    }

    private fun processErrorUpdatedItems(errorUpdatedItems: MutableList<T>): PersistenceLayerError {
        val errorIds = errorUpdatedItems
            .map { it.id }
            .joinToString(separator = ", ")
        val message = "Can't update items with id = [$errorIds]"
        return PersistenceLayerError.IllegalUpdate(message)
    }

}

sealed class PersistenceLayerError(open val message: String?) : DomainError() {

    class IllegalAdd(override val message: String?) : PersistenceLayerError(message)

    class IllegalUpdate(override val message: String?) : PersistenceLayerError(message)

    class EntityNotFound(id: Id<out PersistenceModel>) : PersistenceLayerError("There is no item with id = [$id]")

}