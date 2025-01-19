package me.nikitaklimkin.persistence.account.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.domain.account.Account
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.persistence.account.model.AccountPersistenceModel
import me.nikitaklimkin.persistence.account.model.toPersistenceId
import me.nikitaklimkin.persistence.common.repository.AbstractRepository
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import mu.KotlinLogging
import org.litote.kmongo.eq
import org.litote.kmongo.getCollectionOfName

const val ACCOUNT_COLLECTION = "accounts"

private val log = KotlinLogging.logger {}

class AccountRepository(
    mongoClient: MongoClient,
    properties: DataBaseProperties
) : AccountExtractor, AccountPersistence, AbstractRepository<AccountPersistenceModel> {

    override lateinit var col: MongoCollection<AccountPersistenceModel>

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

    override fun save(userAccounts: Account) {
        log.debug { "Save account with id = [${userAccounts.id}}]" }
        log.trace { "Save account = [$userAccounts]" }
        add(AccountPersistenceModel.fromBusiness(userAccounts))
    }

}