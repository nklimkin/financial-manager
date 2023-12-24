package me.nikitaklimkin.repository

import arrow.core.Either
import arrow.core.left
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.User
import me.nikitaklimkin.access.UserExtractor
import me.nikitaklimkin.access.UserNotFound
import me.nikitaklimkin.access.UserPersistence
import me.nikitaklimkin.configuration.DataBaseProperties
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.TelegramUserPersistenceModel
import me.nikitaklimkin.model.UserPersistenceModel
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollectionOfName

private const val USER_COLLECTION = "user"

class UserRepository(
    private val mongoClient: MongoClient,
    private val properties: DataBaseProperties
) : AbstractRepository<UserPersistenceModel>,
    UserPersistence,
    UserExtractor {

    override lateinit var col: MongoCollection<UserPersistenceModel>

    init {
        val dataBase = mongoClient.getDatabase(properties.dataBaseName)
        col = dataBase.getCollectionOfName(USER_COLLECTION)
    }

    override fun save(user: User) {
        val persistenceModel = UserPersistenceModel.fromBusiness(user)
        add(persistenceModel)
    }

    override fun findByUserName(userName: String): Either<DomainError, User> {
        val userModel = col.findOne(UserPersistenceModel::userName eq userName)
        return userModel?.toBusiness() ?: UserNotFound().left()
    }

    override fun findByTelegramChatId(chatId: Long): Either<DomainError, User> {
        val userModel: UserPersistenceModel? =
            col.findOne(UserPersistenceModel::telegramUser / TelegramUserPersistenceModel::chatId eq chatId)
        return userModel?.toBusiness() ?: UserNotFound().left()
    }
}