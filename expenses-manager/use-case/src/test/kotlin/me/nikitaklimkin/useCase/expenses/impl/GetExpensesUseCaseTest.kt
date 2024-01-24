package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.buildExpenses
import me.nikitaklimkin.domain.buildExpensesDto
import me.nikitaklimkin.domain.buildUserExpenses
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.useCase.expenses.access.ExpensesExtractor
import me.nikitaklimkin.useCase.expenses.access.ExpensesNotFoundError
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class GetExpensesUseCaseTest {

    private val expensesExtractor = mockk<ExpensesExtractor>(relaxed = true)
    private lateinit var getExpensesUseCase: GetExpensesUseCase

    @BeforeEach
    fun setUp() {
        getExpensesUseCase = GetExpensesUseCase(expensesExtractor)
    }

    @Test
    fun `when get expenses for existed user then has success`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns
                buildUserExpenses(
                    initExpenses =
                    mutableListOf(
                        buildExpenses(id = ExpensesId(UUID.randomUUID())),
                        buildExpenses(id = ExpensesId(UUID.randomUUID()))
                    )
                ).right()

        val result = getExpensesUseCase.executeByUser(USER_ID)

        result.isRight() shouldBe true
        val resultPayload = result.getOrNull()!!
        resultPayload.userId shouldBe USER_ID
        resultPayload.details.size shouldBe 2
    }

    @Test
    fun `when not existed expenses then has error`() {
        every { expensesExtractor.findByUserId(USER_ID) } returns ExpensesNotFoundError.left()

        val result = getExpensesUseCase.executeByUser(USER_ID)

        result.isLeft() shouldBe true
    }

    @Test
    fun `when map dto to details then has match result`() {
        val dto = buildExpensesDto()

        val result = dto.toDetails()

        result.id shouldBe dto.id
        result.name shouldBe dto.name
        result.amount shouldBe dto.amount
        result.type shouldBe dto.type
        result.created shouldBe dto.created
        result.description shouldBe dto.description
    }
}