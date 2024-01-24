package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.EXPENSES_ID
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.buildExpenses
import me.nikitaklimkin.domain.buildUserExpenses
import me.nikitaklimkin.useCase.buildUpdateExpenses
import me.nikitaklimkin.useCase.expenses.access.ExpensesExtractor
import me.nikitaklimkin.useCase.expenses.access.ExpensesNotFoundError
import me.nikitaklimkin.useCase.expenses.access.ExpensesPersistence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateExpensesUseCaseTest {

    private val expensesExtractor = mockk<ExpensesExtractor>(relaxed = true)
    private val expensesPersistence = mockk<ExpensesPersistence>(relaxed = true)

    private lateinit var updateExpensesUseCase: UpdateExpensesUseCase

    @BeforeEach
    fun setUp() {
        updateExpensesUseCase = UpdateExpensesUseCase(expensesExtractor, expensesPersistence)
    }

    @Test
    fun `when update existed expenses for existed user then have success result`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns
                buildUserExpenses(
                    initExpenses = mutableListOf(
                        buildExpenses()
                    )
                ).right()

        val result = updateExpensesUseCase.execute(buildUpdateExpenses())

        result.isRight() shouldBe true
    }

    @Test
    fun `when update existed expenses for existed user then update it`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns
                buildUserExpenses(
                    initExpenses = mutableListOf(
                        buildExpenses()
                    )
                ).right()

        val request = buildUpdateExpenses()

        updateExpensesUseCase.execute(request)

        verify { expensesPersistence.update(withArg { it.id shouldBe USER_ID }) }
    }

    @Test
    fun `when update not existed expenses for existed user then has error`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns buildUserExpenses().right()

        val result = updateExpensesUseCase.execute(buildUpdateExpenses())

        result.isLeft() shouldBe true
        verify(exactly = 0) { expensesPersistence.update(any()) }
    }

    @Test
    fun `when update for not existed user then has error`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns ExpensesNotFoundError.left()

        val result = updateExpensesUseCase.execute(buildUpdateExpenses())

        result.isLeft() shouldBe true
        verify(exactly = 0) { expensesPersistence.update(any()) }
    }

    @Test
    fun `when map use case dto to domain then has match result`() {
        val dto = buildUpdateExpenses()

        val result = dto.toDomainDto()

        result.id shouldBe dto.id
        result.name shouldBe dto.name
        result.amount shouldBe dto.amount
        result.type shouldBe dto.type
        result.description shouldBe dto.description
        result.created shouldBe dto.created
    }
}
