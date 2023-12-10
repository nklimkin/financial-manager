package me.nikitaklimkin.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.configuration.DataBaseProperties
import me.nikitaklimkin.model.ExpensesPersistenceModel
import org.litote.kmongo.getCollectionOfName

private const val EXPENSES_COLLECTION = "expenses"

class ExpensesRepository(
    private val mongoClient: MongoClient,
    private val properties: DataBaseProperties
) : AbstractRepository<ExpensesPersistenceModel> {

    override lateinit var col: MongoCollection<ExpensesPersistenceModel>

    init {
        val dataBase = mongoClient.getDatabase(properties.dataBaseName)
        col = dataBase.getCollectionOfName(EXPENSES_COLLECTION)
    }

}