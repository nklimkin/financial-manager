package me.nikitaklimkin.repository

import com.mongodb.client.MongoCollection
import me.nikitaklimkin.model.PersistenceModel
import mu.KotlinLogging
import org.litote.kmongo.deleteMany
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.updateOneById
import java.util.*

private val log = KotlinLogging.logger { }

interface AbstractRepository<T : PersistenceModel> {

    val col: MongoCollection<T>

    fun getById(id: UUID): T {
        log.debug { "get by id $id" }
        return try {
            col.findOne(PersistenceModel::id eq id) ?: throw Exception("No item with that ID exists")
        } catch (t: Throwable) {
            throw Exception("Can't get item")
        }
    }

    fun getAll(): Collection<T> {
        log.debug { "get all" }
        return try {
            val res = col.find()
            res.asIterable().map { it }
        } catch (t: Throwable) {
            throw Exception("Can't get items")
        }
    }

    fun delete(id: UUID): Boolean {
        log.debug { "delete entity with id $id" }
        return try {
            col.findOneAndDelete(PersistenceModel::id eq id) ?: throw Exception("No item with that ID exists")
            true
        } catch (t: Throwable) {
            throw Exception("Can't delete item")
        }
    }

    fun deleteAll() {
        log.debug { "delete all" }
        col.deleteMany()
    }

    fun add(entry: T): T {
        log.debug { "add new entry" }
        log.trace { "new entry is $entry" }
        return try {
            col.insertOne(entry)
            entry
        } catch (t: Throwable) {
            throw Exception("Can't add item", t)
        }
    }

    fun update(entry: T): T {
        log.debug { "update entry" }
        try {
            col.updateOneById(
                entry.id,
                entry
            )
            return entry
        } catch (t: Throwable) {
            throw Exception("Can't update item ${t.message}")
        }
    }


}