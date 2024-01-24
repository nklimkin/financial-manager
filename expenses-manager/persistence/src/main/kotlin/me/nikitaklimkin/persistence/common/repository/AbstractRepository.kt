package me.nikitaklimkin.persistence.common.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.persistence.common.model.PersistenceModel
import mu.KotlinLogging
import org.litote.kmongo.*

private val log = KotlinLogging.logger { }

interface AbstractRepository<T : PersistenceModel> {

    val col: MongoCollection<T>

    fun getById(id: Id<T>): Either<PersistError, T> {
        log.debug { "get by id $id" }
        return try {
            col.findOne(PersistenceModel::id eq id)?.right() ?: PersistError.ItemNotFoundError(id).left()
        } catch (t: Throwable) {
            val message = "There is exception while find item by id $id"
            log.error(message, t)
            PersistError.RuntimePersistError(message).left()
        }
    }

    fun getAll(): Either<PersistError, Collection<T>> {
        log.debug { "get all" }
        return try {
            val res = col.find()
            res.asIterable().map { it }.right()
        } catch (t: Throwable) {
            val message = "There is exception while find all items"
            log.error(message, t)
            PersistError.RuntimePersistError(message).left()
        }
    }

    fun delete(id: Id<T>): Either<PersistError, T> {
        log.debug { "delete entity with id $id" }
        return try {
            col.findOneAndDelete(PersistenceModel::id eq id)?.right() ?: PersistError.ItemNotFoundError(id).left()
        } catch (t: Throwable) {
            val message = "There is exception while delete item with id = [$id]"
            log.error(message, t)
            PersistError.RuntimePersistError(message).left()
        }
    }

    fun deleteAll(): Either<PersistError, Unit> {
        log.debug { "delete all" }
        return try {
            col.deleteMany()
            Unit.right()
        } catch (t: Throwable) {
            val message = "There is exception while delete all items"
            log.error(message, t)
            PersistError.RuntimePersistError(message).left()
        }
    }

    fun add(entry: T): Either<PersistError, T> {
        log.debug { "add new entry" }
        log.trace { "new entry is $entry" }
        return try {
            col.insertOne(entry)
            entry.right()
        } catch (t: Throwable) {
            val message = "There is exception while add new item"
            log.error(message, t)
            PersistError.RuntimePersistError(message).left()
        }
    }

    fun addAll(entries: Collection<T>): Either<PersistError, Collection<T>> {
        log.debug { "add ${entries.size} entries" }
        log.trace { "Add $entries" }
        return try {
            col.insertMany(entries.toMutableList())
            entries.right()
        } catch (t: Throwable) {
            val message = "There is exception while add items"
            log.error(message, t)
            PersistError.RuntimePersistError(message).left()
        }
    }

    fun update(entry: T): Either<PersistError, T> {
        log.debug { "update entry" }
        try {
            val updateResult = col.updateOneById(
                entry.id,
                entry
            )
            if (updateResult.modifiedCount == 0L) {
                return PersistError.ItemNotFoundError(entry.id).left()
            }
            return entry.right()
        } catch (t: Throwable) {
            val message = "Can't update item with id = [${entry.id}]"
            log.error(message, t)
            return PersistError.RuntimePersistError(message).left()
        }
    }

    fun updateAll(entries: Collection<T>): Either<PersistError, Collection<T>> {
        log.debug { "Update ${entries.size} entries" }
        log.trace { "Update $entries" }
        try {
            val successfullyUpdatedItems = mutableListOf<T>()
            val errorUpdatedItems = mutableListOf<T>()

            for (entry in entries) {
                update(entry)
                    .fold(
                        { _ -> errorUpdatedItems.add(entry) },
                        { right -> successfullyUpdatedItems.add(right) }
                    )
            }

            return if (errorUpdatedItems.isNotEmpty()) {
                processErrorUpdatedItems(errorUpdatedItems).left()
            } else {
                successfullyUpdatedItems.right()
            }
        } catch (t: Throwable) {
            val message = "Can't update all items"
            log.error(message, t)
            return PersistError.RuntimePersistError(message).left()
        }
    }

    private fun processErrorUpdatedItems(errorUpdatedItems: MutableList<T>): PersistError {
        val errorIds = errorUpdatedItems
            .map { it.id }
            .joinToString(separator = ", ")
        val message = "Can't update items with id = [$errorIds]"
        return PersistError.RuntimePersistError(message)
    }

}

sealed class PersistError(open val message: String) : DomainError() {

    class RuntimePersistError(override val message: String) : PersistError(message)

    class ItemNotFoundError(id: Id<out PersistenceModel>) : PersistError("There is no item with id = [$id]")

}