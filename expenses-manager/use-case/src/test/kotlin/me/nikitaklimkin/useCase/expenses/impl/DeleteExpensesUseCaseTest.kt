package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.nikitaklimkin.domain.EXPENSES_ID
import me.nikitaklimkin.useCase.expenses.access.ExpensesNotFoundError
import me.nikitaklimkin.useCase.expenses.access.ExpensesPersistence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteExpensesUseCaseTest {

    private val expensesPersistence = mockk<ExpensesPersistence>()
    private lateinit var deleteExpensesUseCase: DeleteExpensesUseCase

    @BeforeEach
    fun setUp() {
        deleteExpensesUseCase = DeleteExpensesUseCase(expensesPersistence)
    }

    @Test
    fun `when delete existed expenses then has success`() {
        every { expensesPersistence.delete(EXPENSES_ID) } returns Unit.right()

        val result = deleteExpensesUseCase.execute(EXPENSES_ID)

        result.isRight() shouldBe true
    }

    @Test
    fun `when delete not existed expenses then has error`() {
        every { expensesPersistence.delete(EXPENSES_ID) } returns ExpensesNotFoundError.left()

        val result = deleteExpensesUseCase.execute(EXPENSES_ID)

        result.isLeft() shouldBe true
    }
}