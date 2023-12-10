package me.nikitaklimkin.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.User
import me.nikitaklimkin.access.UserPersistence
import me.nikitaklimkin.configuration.DataBaseProperties
import me.nikitaklimkin.model.UserPersistenceModel
import org.litote.kmongo.getCollectionOfName

private const val USER_COLLECTION = "user"

class UserRepository(
    private val mongoClient: MongoClient,
    private val properties: DataBaseProperties
) : AbstractRepository<UserPersistenceModel>,
    UserPersistence {

    override lateinit var col: MongoCollection<UserPersistenceModel>

    init {
        val dataBase = mongoClient.getDatabase(properties.dataBaseName)
        col = dataBase.getCollectionOfName(USER_COLLECTION)
    }

    override fun save(user: User) {
        val persistenceModel = UserPersistenceModel.fromBusiness(user)
        add(persistenceModel)
    }
}