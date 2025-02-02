package me.nikitaklimkin.persistence.transactions.repository

import arrow.core.Either
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.domain.account.Account
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.Transaction
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.domain.transaction.TransactionIdGenerator
import me.nikitaklimkin.persistence.account.model.toPersistenceId
import me.nikitaklimkin.persistence.common.repository.AbstractRepository
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.transactions.model.TransactionPersistenceModel
import me.nikitaklimkin.persistence.transactions.model.toPersistenceId
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistenceError
import mu.KotlinLogging
import org.litote.kmongo.eq
import org.litote.kmongo.getCollectionOfName
import java.util.*

private const val TRANSACTION_COLLECTION = "transactions"

private val log = KotlinLogging.logger { }

class TransactionsRepository(
    mongoClient: MongoClient,
    properties: DataBaseProperties
) : AbstractRepository<TransactionPersistenceModel>,
    TransactionExtractor,
    TransactionPersistence,
    TransactionIdGenerator {

    override var col: MongoCollection<TransactionPersistenceModel>

    init {
        val dataBase = mongoClient.getDatabase(properties.dataBaseName)
        col = dataBase.getCollectionOfName(TRANSACTION_COLLECTION)
    }

    override fun findById(transactionId: TransactionId): Transaction? {
        log.debug { "Find transaction with id = [$transactionId]" }
        val transaction = getById(transactionId.toPersistenceId())?.toBusiness()
        if (transaction?.isLeft() == true) {
            throw IllegalStateException("Invalid entity in db with id = [$transactionId]")
        }
        return transaction?.getOrNull()
    }

    override fun findByAccount(account: Account): Collection<Transaction> {
        log.debug { "Find transaction with account with id = [${account.id}]" }
        val transactions = col.find(TransactionPersistenceModel::accountId eq account.id.toPersistenceId())
            .map { it.toBusiness() }
        val invalidEntities = transactions.filter { it.isLeft() }
        if (invalidEntities.isNotEmpty()) {
            throw IllegalStateException("Invalid entities in db for account with id [${account.id}]")
        }
        return transactions
            .filter { it.isRight() }
            .map { it.getOrNull()!! }
    }

    override fun save(transaction: Transaction): Either<TransactionPersistenceError.TransactionAlreadyExists, Unit> {
        log.debug { "Save transaction with id = [${transaction.id}]" }
        log.trace { "Save transaction = [$transaction]" }
        return add(TransactionPersistenceModel.fromBusiness(transaction))
            .mapLeft { TransactionPersistenceError.TransactionAlreadyExists }
            .map { Unit }
    }

    override fun update(transaction: Transaction): Either<TransactionPersistenceError.TransactionNotFound, Unit> {
        log.debug { "Update transaction with id = [${transaction.id}]" }
        log.trace { "Update transaction = [$transaction]" }
        return update(TransactionPersistenceModel.fromBusiness(transaction))
            .mapLeft { TransactionPersistenceError.TransactionNotFound }
            .map { Unit }
    }

    override fun generate(): TransactionId {
        return TransactionId.from(UUID.randomUUID().toString()).getOrNull()
            ?: throw RuntimeException("Illegal id for transaction domain")
    }

}