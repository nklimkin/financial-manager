package me.nikitaklimkin.persistence.repository

import arrow.core.Either
import arrow.core.left
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.domain.User
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.model.TelegramUserPersistenceModel
import me.nikitaklimkin.persistence.model.UserPersistenceModel
import me.nikitaklimkin.useCase.access.UserExtractor
import me.nikitaklimkin.useCase.access.UserNotFound
import me.nikitaklimkin.useCase.access.UserPersistence
import mu.KotlinLogging
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollectionOfName

private const val USER_COLLECTION = "user"

private val log = KotlinLogging.logger {}

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
        log.debug { "Find user with name = [$userName]" }
        val userModel = col.findOne(UserPersistenceModel::userName eq userName)
        return userModel?.toBusiness() ?: UserNotFound.left()
    }

    override fun findByTelegramChatId(chatId: Long): Either<DomainError, User> {
        log.debug { "Find user with chatId = [$chatId]" }
        val userModel: UserPersistenceModel? =
            col.findOne(UserPersistenceModel::telegramUser / TelegramUserPersistenceModel::chatId eq chatId)
        return userModel?.toBusiness() ?: UserNotFound.left()
    }
}