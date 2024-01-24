package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.ExpensesIdGeneratorFixtures
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.buildUserExpenses
import me.nikitaklimkin.useCase.buildAddExpenses
import me.nikitaklimkin.useCase.expenses.access.ExpensesExtractor
import me.nikitaklimkin.useCase.expenses.access.ExpensesNotFoundError
import me.nikitaklimkin.useCase.expenses.access.ExpensesPersistence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddNewExpensesUseCaseTest {

    private val expensesPersistence = mockk<ExpensesPersistence>(relaxed = true)
    private val expensesExtractor = mockk<ExpensesExtractor>(relaxed = true)
    private val expensesIdGenerator = ExpensesIdGeneratorFixtures()

    private lateinit var addNewExpensesUseCase: AddNewExpensesUseCase

    @BeforeEach
    fun setUp() {
        addNewExpensesUseCase = AddNewExpensesUseCase(
            expensesExtractor,
            expensesPersistence,
            expensesIdGenerator
        )
    }

    @Test
    fun `when add expenses for existed user then has success`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns buildUserExpenses().right()
        every { expensesPersistence.save(any()) } returns Unit.right()

        val result = addNewExpensesUseCase.execute(buildAddExpenses())

        result.isRight() shouldBe true
    }

    @Test
    fun `when add expenses for existed user then persists it`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns buildUserExpenses().right()

        addNewExpensesUseCase.execute(buildAddExpenses())

        verify {
            expensesIdGenerator.generate()
            expensesPersistence.save(withArg {
                it.id shouldBe USER_ID
            })
        }
    }

    @Test
    fun `when add expenses for not existed user then has error`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns ExpensesNotFoundError.left()

        val result = addNewExpensesUseCase.execute(buildAddExpenses())

        result.isLeft() shouldBe true
        verify(exactly = 0) { expensesPersistence.save(any()) }
    }

    @Test
    fun `when map use case dto to domain dto then has match result`() {
        val dto = buildAddExpenses()

        val result = dto.toDomainDto()

        result.name shouldBe dto.name
        result.amount shouldBe dto.amount
        result.type shouldBe dto.type
        result.created shouldBe dto.created
        result.description shouldBe dto.description
    }
}