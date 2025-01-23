package me.nikitaklimkin.persistence.account.repository

import com.mongodb.client.MongoCollection
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.account.BrokerageAccount
import me.nikitaklimkin.domain.account.CardAccount
import me.nikitaklimkin.domain.account.DepositAccount
import me.nikitaklimkin.domain.account.PiggyBankAccount
import me.nikitaklimkin.persistence.*
import me.nikitaklimkin.persistence.account.model.AccountPersistenceModel
import me.nikitaklimkin.persistence.account.model.toAccountId
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollectionOfName
import org.litote.kmongo.save
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Testcontainers
class AccountRepositoryTest {

    companion object {

        @Container
        val mongoContainer = MongoDBContainer("mongo:4.0.10")

    }

    private var repository: AccountRepository
    private var collection: MongoCollection<AccountPersistenceModel>

    init {
        val client = KMongo.createClient(mongoContainer.replicaSetUrl)
        collection = client.getDatabase("test-accounts")
            .getCollectionOfName("accounts")
        repository = AccountRepository(client, DataBaseProperties("test-accounts"))
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @TestFactory
    fun `when save account then has match result`() = listOf(
        depositAccount(
            ACCOUNT_ID,
            TEST_PERSISTENCE_OPENED_DATE,
            TEST_PERSISTENCE_CLOSED_DATE
        ) to buildDepositAccountPersistenceModel(ACCOUNT_ID),
        cardAccount(ACCOUNT_ID_2) to buildCardAccountPersistenceModel(ACCOUNT_ID_2),
        piggyAccount(ACCOUNT_ID_3) to buildPiggyAccountPersistenceModel(ACCOUNT_ID_3),
        brokerAccount(ACCOUNT_ID_4) to buildBrokerageAccountPersistenceModel(ACCOUNT_ID_4)
    )
        .map { (input, expectedPersisted) ->

            DynamicTest.dynamicTest(
                "when save account with type = ${input.javaClass} then save match data"
            ) {

                val result = repository.save(input)
                result.isRight() shouldBe true

                val persisted = collection.find()
                    .toList()
                    .filter { it.id.toAccountId().getOrNull()!!.toUuid() == input.id.toUuid() }

                persisted.size shouldBe 1
                persisted.first() shouldBe expectedPersisted

            }
        }

    @Test
    fun `when save duplicate then has left value`() {
        val source = buildDepositAccountPersistenceModel(ACCOUNT_ID)
        collection.save(source)

        val account = depositAccount(ACCOUNT_ID, TEST_PERSISTENCE_OPENED_DATE, TEST_PERSISTENCE_CLOSED_DATE)

        val result = repository.save(account)

        result.isLeft() shouldBe true
    }

    @Test
    fun `when update existed account then has match result`() {
        val source = buildDepositAccountPersistenceModel(ACCOUNT_ID)
        collection.save(source)

        val newDate = OffsetDateTime.of(
            LocalDateTime.of(1999, 5, 5, 10, 10, 10),
            ZoneOffset.UTC
        )
        val updatedAccount = depositAccount(id = ACCOUNT_ID, closedDate = newDate, openedDateTime = newDate)

        val result = repository.update(updatedAccount)

        result.isRight() shouldBe true

        val persisted = collection.find()
            .toList()
            .filter { it.id.toAccountId().getOrNull()!!.toUuid() == ACCOUNT_ID.toUuid() }

        persisted.size shouldBe 1
        persisted.first()?.closedDate shouldBe newDate
    }

    @Test
    fun `when update not existed account then has match result`() {
        val newDate = OffsetDateTime.of(
            LocalDateTime.of(1999, 5, 5, 10, 10, 10),
            ZoneOffset.UTC
        )
        val updatedAccount = depositAccount(id = ACCOUNT_ID, closedDate = newDate, openedDateTime = newDate)

        val result = repository.update(updatedAccount)

        result.isLeft() shouldBe true
    }

    @Test
    fun `when find by existed user id then has match result`() {
        collection.save(buildDepositAccountPersistenceModel())
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_2))
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_3, USER_ID_2))

        val result = repository.findByUser(buildUser(USER_ID))

        result.size shouldBe 2
        result.map { it.id.toUuid() } shouldContainAll listOf(ACCOUNT_ID.toUuid(), ACCOUNT_ID_2.toUuid())
    }

    @Test
    fun `when find by not existed user then has match result`() {
        collection.save(buildDepositAccountPersistenceModel())
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_2))
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_3))

        val result = repository.findByUser(buildUser(USER_ID_2))

        result.size shouldBe 0
    }

    @Test
    fun `when find by id then has match result`() {
        collection.save(buildDepositAccountPersistenceModel())
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_2))
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_3, USER_ID_2))

        val result = repository.findById(ACCOUNT_ID)

        result shouldNotBe null
        result?.id?.toUuid() shouldBe ACCOUNT_ID.toUuid()
    }

    @Test
    fun `when find by not existed id then has match result`() {
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_2))
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_3, USER_ID_2))

        val result = repository.findById(ACCOUNT_ID)

        result shouldBe null
    }

    @Test
    fun `when check is account exists and it exists then return true`() {
        collection.save(buildDepositAccountPersistenceModel())
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_2))
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_3, USER_ID_2))

        val result = repository.isAccountExists(ACCOUNT_ID)

        result shouldBe true
    }

    @Test
    fun `when check is account exists and it not exists then return false`() {
        collection.save(buildDepositAccountPersistenceModel())
        collection.save(buildDepositAccountPersistenceModel(ACCOUNT_ID_2))

        val result = repository.isAccountExists(ACCOUNT_ID_3)

        result shouldBe false
    }

    @Test
    fun `when map persistence deposit account to business then has match result`() {
        val source = buildDepositAccountPersistenceModel();

        val result = source.toBusiness()

        result.isRight() shouldBe true
        (result.getOrNull()!! is DepositAccount) shouldBe true
        val resultAccount = result.getOrNull()!! as DepositAccount
        resultAccount.id.toUuid() shouldBe ACCOUNT_ID.toUuid()
        resultAccount.userId.toUuid() shouldBe USER_ID.toUuid()
        resultAccount.bankName shouldBe TEST_BANK_NAME
        resultAccount.description shouldBe TEST_ACCOUNT_DESCRIPTION
        resultAccount.initialBalance shouldBe TEST_MONEY_AMOUNT
        resultAccount.expectedFinalBalance shouldBe TEST_MONEY_AMOUNT_2
        resultAccount.openedDate shouldBe TEST_PERSISTENCE_OPENED_DATE
        resultAccount.closedDate shouldBe TEST_PERSISTENCE_CLOSED_DATE
        resultAccount.interest shouldBe TEST_INTEREST
        resultAccount.active shouldBe true
    }

    @Test
    fun `when map persistence card account to business then has match result`() {
        val source = buildCardAccountPersistenceModel();

        val result = source.toBusiness()

        result.isRight() shouldBe true
        (result.getOrNull()!! is CardAccount) shouldBe true
        val resultAccount = result.getOrNull()!! as CardAccount
        resultAccount.id.toUuid() shouldBe ACCOUNT_ID.toUuid()
        resultAccount.userId.toUuid() shouldBe USER_ID.toUuid()
        resultAccount.bankName shouldBe TEST_BANK_NAME
        resultAccount.description shouldBe TEST_ACCOUNT_DESCRIPTION
        resultAccount.balance() shouldBe TEST_MONEY_AMOUNT
        resultAccount.active shouldBe true
    }

    @Test
    fun `when map persistence piggy account to business then has match result`() {
        val source = buildPiggyAccountPersistenceModel();

        val result = source.toBusiness()

        result.isRight() shouldBe true
        (result.getOrNull()!! is PiggyBankAccount) shouldBe true
        val resultAccount = result.getOrNull()!! as PiggyBankAccount
        resultAccount.id.toUuid() shouldBe ACCOUNT_ID.toUuid()
        resultAccount.userId.toUuid() shouldBe USER_ID.toUuid()
        resultAccount.bankName shouldBe TEST_BANK_NAME
        resultAccount.description shouldBe TEST_ACCOUNT_DESCRIPTION
        resultAccount.balance() shouldBe TEST_MONEY_AMOUNT
        resultAccount.interest shouldBe TEST_INTEREST
        resultAccount.active shouldBe true
    }

    @Test
    fun `when map persistence brokerage account to business then has match result`() {
        val source = buildBrokerageAccountPersistenceModel();

        val result = source.toBusiness()

        result.isRight() shouldBe true
        (result.getOrNull()!! is BrokerageAccount) shouldBe true
        val resultAccount = result.getOrNull()!! as BrokerageAccount
        resultAccount.id.toUuid() shouldBe ACCOUNT_ID.toUuid()
        resultAccount.userId.toUuid() shouldBe USER_ID.toUuid()
        resultAccount.bankName shouldBe TEST_BANK_NAME
        resultAccount.description shouldBe TEST_ACCOUNT_DESCRIPTION
        resultAccount.balance() shouldBe TEST_MONEY_AMOUNT
        resultAccount.active shouldBe true
    }
}