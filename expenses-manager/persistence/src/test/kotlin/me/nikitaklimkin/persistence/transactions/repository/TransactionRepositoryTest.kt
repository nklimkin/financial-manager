package me.nikitaklimkin.persistence.transactions.repository

import com.mongodb.client.MongoCollection
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.transaction.TransactionName
import me.nikitaklimkin.persistence.account.model.toPersistenceId
import me.nikitaklimkin.persistence.buildTransactionPersistenceModel
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.transactions.model.TransactionPersistenceModel
import me.nikitaklimkin.persistence.transactions.model.toPersistenceId
import me.nikitaklimkin.persistence.transactions.model.toTransactionId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollectionOfName
import org.litote.kmongo.save
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class TransactionRepositoryTest {

    companion object {

        @Container
        val mongoContainer = MongoDBContainer("mongo:4.0.10")

    }

    private var repository: TransactionsRepository
    private var collection: MongoCollection<TransactionPersistenceModel>

    init {
        val client = KMongo.createClient(mongoContainer.replicaSetUrl)
        collection = client.getDatabase("test-transactions")
            .getCollectionOfName("transactions")
        repository = TransactionsRepository(client, DataBaseProperties("test-transactions"))
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `when save new transaction then has new elements in document`() {
        val transactions = buildTransaction()

        val result = repository.save(transactions)

        result.isRight() shouldBe true

        val persistedTransactions = collection.find().toList()
        persistedTransactions shouldNotBe null
        persistedTransactions.size shouldBe 1
        persistedTransactions.map { it.id.toTransactionId() } shouldContainAll listOf(transactions.id)
    }

    @Test
    fun `when get existed elements by id then has match result`() {
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID.toPersistenceId(),
                accountId = ACCOUNT_ID.toPersistenceId(),
                name = "unique-test-name"
            )
        )
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID_2.toPersistenceId(),
                accountId = ACCOUNT_ID.toPersistenceId()
            )
        )
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID_3.toPersistenceId(),
                accountId = ACCOUNT_ID_2.toPersistenceId()
            )
        )

        val result = repository.findById(TRANSACTION_ID)

        result shouldNotBe null
        result?.name shouldBe TransactionName.from("unique-test-name").getOrNull()!!
    }

    @Test
    fun `when get elements for not id then has empty result`() {
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID.toPersistenceId(),
                accountId = ACCOUNT_ID.toPersistenceId()
            )
        )

        val result = repository.findById(TRANSACTION_ID_2)

        result shouldBe null
    }

    @Test
    fun `when get existed elements for account then has match result`() {
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID.toPersistenceId(),
                accountId = ACCOUNT_ID.toPersistenceId()
            )
        )
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID_2.toPersistenceId(),
                accountId = ACCOUNT_ID.toPersistenceId()
            )
        )
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID_3.toPersistenceId(),
                accountId = ACCOUNT_ID_2.toPersistenceId()
            )
        )

        val result = repository.findByAccount(cardAccount(id = ACCOUNT_ID))

        result.size shouldBe 2
        result.map { it.id.toUuid().toString() } shouldContainAll listOf(
            TRANSACTION_ID.toUuid().toString(),
            TRANSACTION_ID_2.toUuid().toString()
        )
    }

    @Test
    fun `when get elements for not existed account then has empty result`() {
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID.toPersistenceId(),
                accountId = ACCOUNT_ID.toPersistenceId()
            )
        )
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID_2.toPersistenceId(),
                accountId = ACCOUNT_ID.toPersistenceId()
            )
        )
        collection.save(
            buildTransactionPersistenceModel(
                id = TRANSACTION_ID_3.toPersistenceId(),
                accountId = ACCOUNT_ID_2.toPersistenceId()
            )
        )

        val result = repository.findByAccount(cardAccount(id = ACCOUNT_ID_3))

        result.isEmpty() shouldBe true
    }
}
