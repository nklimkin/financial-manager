package me.nikitaklimkin.persistence.user.repository

import arrow.core.Either
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.user.UserName
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.persistence.common.repository.AbstractRepository
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.user.model.UserPersistenceModel
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import me.nikitaklimkin.useCase.user.access.UserExtractor
import me.nikitaklimkin.useCase.user.access.UserPersistence
import mu.KotlinLogging
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollectionOfName

private const val USER_COLLECTION = "user"

private val log = KotlinLogging.logger {}

class UserRepository(
    mongoClient: MongoClient,
    properties: DataBaseProperties
) : AbstractRepository<UserPersistenceModel>,
    UserPersistence,
    UserExtractor {

    override lateinit var col: MongoCollection<UserPersistenceModel>

    init {
        val dataBase = mongoClient.getDatabase(properties.dataBaseName)
        col = dataBase.getCollectionOfName(USER_COLLECTION)
    }

    override fun save(user: User): Either<DomainError, Unit> {
        val persistenceModel = UserPersistenceModel.fromBusiness(user)
        return add(persistenceModel)
            .map { Unit }
    }

    override fun findByUserName(userName: UserName): User? {
        log.debug { "Find user with name = [$userName]" }
        val user = col.findOne(UserPersistenceModel::userName eq userName.getValue())?.toBusiness()
        if (user?.isLeft() == true) {
            throw IllegalStateException("Invalid entity in db with userName = [$userName]")
        }
        return user?.getOrNull()
    }

    override fun findByUserId(userId: UserId): User? {
        log.debug { "Find user with id $userId" }
        val user = getById(userId.toPersistenceId())?.toBusiness()
        if (user?.isLeft() == true) {
            throw IllegalStateException("Invalid entity in db with id = [$userId]")
        }
        return user?.getOrNull()
    }
}