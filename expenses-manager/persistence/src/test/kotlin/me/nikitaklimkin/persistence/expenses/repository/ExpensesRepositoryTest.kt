package me.nikitaklimkin.persistence.expenses.repository

import com.mongodb.client.MongoCollection
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.ExpensesName
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.persistence.buildExpensesPersistenceModel
import me.nikitaklimkin.persistence.common.repository.PersistError
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.expenses.model.ExpensesPersistenceModel
import me.nikitaklimkin.persistence.expenses.model.toPersistenceId
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.Id
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollectionOfName
import org.litote.kmongo.save
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@Testcontainers
class ExpensesRepositoryTest {

    companion object {

        @Container
        val mongoContainer = MongoDBContainer("mongo:4.0.10")

    }

    private lateinit var repository: ExpensesRepository
    private lateinit var collection: MongoCollection<ExpensesPersistenceModel>

    init {
        val client = KMongo.createClient(mongoContainer.replicaSetUrl)
        collection = client.getDatabase("test-expenses")
            .getCollectionOfName("expenses")
        repository = ExpensesRepository(client, DataBaseProperties("test-expenses"))
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `when save new expenses then has new elements in document`() {
        val firstExpensesId = ExpensesId(UUID.randomUUID())
        val secondExpensesId = ExpensesId(UUID.randomUUID())

        val firstExpensesNameToSave = ExpensesName.from("sport").getOrNull()!!
        val secondExpensesNameToSave = ExpensesName.from("books").getOrNull()!!

        val userExpenses = buildUserExpensesWithExpensesToSave(
            USER_ID,
            listOf(buildExpenses(firstExpensesId), buildExpenses(secondExpensesId)),
            listOf(buildSaveExpensesDto(firstExpensesNameToSave), buildSaveExpensesDto(secondExpensesNameToSave))
        )

        val result = repository.save(userExpenses)

        result.isRight() shouldBe true

        val persistedExpenses = collection.find().toList()
        persistedExpenses shouldNotBe null
        persistedExpenses.size shouldBe 2
        persistedExpenses.map { it.name } shouldContainAll listOf("sport", "books")
    }

    @Test
    fun `when update expenses then update elements in document`() {
        val firstPersistedId = ExpensesId(UUID.randomUUID())
        val secondPersistedId = ExpensesId(UUID.randomUUID())

        collection.save(
            buildExpensesPersistenceModel(
                expensesId = firstPersistedId.toPersistenceId(),
                name = "sport"
            )
        )
        collection.save(
            buildExpensesPersistenceModel(
                expensesId = secondPersistedId.toPersistenceId(),
                name = "book"
            )
        )

        val userExpenses = buildUserExpensesWithExpensesToUpdate(
            USER_ID,
            listOf(buildExpenses(), buildExpenses(firstPersistedId), buildExpenses(secondPersistedId)),
            listOf(
                buildUpdateExpensesDto(
                    id = firstPersistedId,
                    name = ExpensesName.from("traveling").getOrNull()!!
                ),
                buildUpdateExpensesDto(
                    id = secondPersistedId,
                    name = ExpensesName.from("ps5").getOrNull()!!
                )
            )
        )

        val result = repository.update(userExpenses)

        result.isRight() shouldBe true

        val persistedExpenses = collection.find().toList()
        persistedExpenses shouldNotBe null
        persistedExpenses.size shouldBe 2

        val persistenceModelById = persistedExpenses.groupBy { it.id }

        checkPersistenceById(firstPersistedId, "traveling", persistenceModelById)
        checkPersistenceById(secondPersistedId, "ps5", persistenceModelById)
    }

    private fun checkPersistenceById(
        firstPersistedId: ExpensesId,
        expectedName: String,
        persistenceModelById: Map<Id<ExpensesPersistenceModel>, List<ExpensesPersistenceModel>>
    ) {
        val persisted = persistenceModelById[firstPersistedId.toPersistenceId()]
        persisted shouldNotBe null
        persisted?.size shouldBe 1
        persisted?.get(0)?.name shouldBe expectedName
    }

    @Test
    fun `then update not existed element in document then has error result`() {
        val expensesId = ExpensesId(UUID.randomUUID())
        val userExpenses = buildUserExpensesWithExpensesToUpdate(
            USER_ID,
            listOf(buildExpenses(expensesId)),
            listOf(buildUpdateExpensesDto(expensesId))
        )

        val result = repository.update(userExpenses)

        result.isLeft() shouldBe true
        result.leftOrNull().shouldBeInstanceOf<PersistError>()
    }

    @Test
    fun `then delete existed element then there is no such element in document`() {
        val expensesId = ExpensesId(UUID.randomUUID())

        collection.save(buildExpensesPersistenceModel(expensesId.toPersistenceId()))
        collection.save(buildExpensesPersistenceModel())

        val result = repository.delete(expensesId)

        result.isRight() shouldBe true
        result.getOrNull()!!.id shouldBe expensesId

        collection.find().toList().size shouldBe 1
    }

    @Test
    fun `when delete not existed element in document then has error result`() {
        collection.save(buildExpensesPersistenceModel())
        collection.save(buildExpensesPersistenceModel())

        val result = repository.delete(ExpensesId(UUID.randomUUID()))

        result.isLeft() shouldBe true
        result.leftOrNull().shouldBeInstanceOf<PersistError.ItemNotFoundError>()
    }

    @Test
    fun `when get existed elements for user then has match result`() {
        val firstId = ExpensesId(UUID.randomUUID())
        val secondId = ExpensesId(UUID.randomUUID())
        val userId = UserId(UUID.randomUUID())

        collection.save(
            buildExpensesPersistenceModel(
                expensesId = firstId.toPersistenceId(),
                userId = userId.toPersistenceId()
            )
        )
        collection.save(
            buildExpensesPersistenceModel(
                expensesId = secondId.toPersistenceId(),
                userId = userId.toPersistenceId()
            )
        )

        collection.save(buildExpensesPersistenceModel())

        val result = repository.getUserExpenses(userId)
        result.isRight() shouldBe true
        result.getOrNull()?.getExpenses()?.size shouldBe 2
    }

    @Test
    fun `when get elements for not existed user then has error result`() {
        collection.save(buildExpensesPersistenceModel(userId = UserId(UUID.randomUUID()).toPersistenceId()))

        val result = repository.getUserExpenses(USER_ID)

        result.isLeft() shouldBe true
        result.leftOrNull().shouldBeInstanceOf<ExpensesNotFoundError>()
    }
}
