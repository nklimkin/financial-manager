package me.nikitaklimkin.persistence.account.repository

import arrow.core.Either
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.domain.account.Account
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.account.AccountIdGenerator
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.persistence.account.model.AccountPersistenceModel
import me.nikitaklimkin.persistence.account.model.toPersistenceId
import me.nikitaklimkin.persistence.common.repository.AbstractRepository
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.account.access.AccountPersistenceError
import mu.KotlinLogging
import org.litote.kmongo.eq
import org.litote.kmongo.getCollectionOfName
import java.util.UUID

const val ACCOUNT_COLLECTION = "accounts"

private val log = KotlinLogging.logger {}

class AccountRepository(
    mongoClient: MongoClient,
    properties: DataBaseProperties
) : AccountExtractor,
    AccountPersistence,
    AccountIdGenerator,
    AbstractRepository<AccountPersistenceModel> {

    override var col: MongoCollection<AccountPersistenceModel>

    init {
        val dataBase = mongoClient.getDatabase(properties.dataBaseName)
        col = dataBase.getCollectionOfName(ACCOUNT_COLLECTION)
    }

    override fun findByUser(user: User): Collection<Account> {
        log.debug { "Find accounts by user with id = [${user.id}]" }
        val persisted = col.find(AccountPersistenceModel::userId eq user.id.toPersistenceId())
            .map { it.toBusiness() }
        val invalidEntities = persisted.filter { it.isLeft() }
        if (invalidEntities.isNotEmpty()) {
            throw IllegalStateException("Invalid entities in db for user with id [${user.id}]")
        }
        return persisted
            .filter { it.isRight() }
            .map { it.getOrNull()!! }
    }

    override fun findById(accountId: AccountId): Account? {
        log.debug { "Find account with id = [$accountId]" }
        val persisted = getById(accountId.toPersistenceId())?.toBusiness()
        if (persisted?.isLeft() == true) {
            throw IllegalStateException("Invalid entities in db for with id [${accountId}]")
        }
        return persisted?.getOrNull()
    }

    override fun isAccountExists(accountId: AccountId): Boolean {
        log.debug { "Is account with id = [$accountId] exists" }
        return getById(accountId.toPersistenceId()) != null
    }

    override fun save(userAccounts: Account): Either<AccountPersistenceError.AccountAlreadyExists, Unit> {
        log.debug { "Save account with id = [${userAccounts.id}}]" }
        log.trace { "Save account = [$userAccounts]" }
        return add(AccountPersistenceModel.fromBusiness(userAccounts))
            .mapLeft {
                AccountPersistenceError.AccountAlreadyExists
            }
            .map { Unit }
    }

    override fun update(userAccounts: Account): Either<AccountPersistenceError.AccountNotFound, Unit> {
        log.debug { "Update account with id = [${userAccounts.id}}]" }
        log.trace { "Update account = [$userAccounts]" }
        return update(AccountPersistenceModel.fromBusiness(userAccounts))
            .mapLeft {
                AccountPersistenceError.AccountNotFound
            }
            .map { Unit }
    }

    override fun generate(): AccountId {
        return AccountId.from(UUID.randomUUID().toString()).getOrNull()
            ?: throw RuntimeException("Illegal id for account domain")
    }

}