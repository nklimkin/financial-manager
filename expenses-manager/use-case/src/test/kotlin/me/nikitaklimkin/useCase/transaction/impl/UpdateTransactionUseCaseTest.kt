package me.nikitaklimkin.useCase.transaction.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.buildUpdateTransactionDTO
import me.nikitaklimkin.useCase.transaction.UpdateTransactionError
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import me.nikitaklimkin.useCase.transaction.impl.UpdateTransactionUseCase
import me.nikitaklimkin.useCase.transaction.toDomainDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateTransactionUseCaseTest {

    private val accountExtractor = mockk<AccountExtractor>(relaxed = true)
    private val transactionExtractor = mockk<TransactionExtractor>(relaxed = true)
    private val transactionPersistence = mockk<TransactionPersistence>(relaxed = true)

    private lateinit var updateTransactionUseCase: UpdateTransactionUseCase

    @BeforeEach
    fun setUp() {
        updateTransactionUseCase =
            UpdateTransactionUseCase(accountExtractor, transactionExtractor, transactionPersistence)
    }

    @Test
    fun `when update existed transaction for existed user then have success result`() {
        every { transactionExtractor.findById(TRANSACTION_ID) } returns buildTransaction()
        every { accountExtractor.findById(ACCOUNT_ID) } returns depositAccount()

        val result = updateTransactionUseCase.execute(buildUpdateTransactionDTO())

        result.isRight() shouldBe true
    }

    @Test
    fun `when update existed transaction for existed user then update it`() {
        every { transactionExtractor.findById(TRANSACTION_ID) } returns buildTransaction()
        every { accountExtractor.findById(ACCOUNT_ID) } returns depositAccount()

        updateTransactionUseCase.execute(buildUpdateTransactionDTO())

        verify { transactionPersistence.save(withArg { it.id shouldBe TRANSACTION_ID }) }
    }

    @Test
    fun `when update not existed transaction then has error`() {
        every { transactionExtractor.findById(TRANSACTION_ID) } returns null

        val result = updateTransactionUseCase.execute(buildUpdateTransactionDTO())

        result.isLeft() shouldBe true
        result.leftOrNull()!! shouldBe UpdateTransactionError.TransactionNotFound
    }

    @Test
    fun `when update for not valid user then has error`() {
        every { transactionExtractor.findById(TRANSACTION_ID) } returns buildTransaction()
        every { accountExtractor.findById(ACCOUNT_ID) } returns depositAccount()

        val result = updateTransactionUseCase.execute(buildUpdateTransactionDTO(userId = USER_ID_2))

        result.isLeft() shouldBe true
        result.leftOrNull()!! shouldBe UpdateTransactionError.TransactionNotFound
        verify(atMost = 0, atLeast = 0) { transactionPersistence.save(any()) }
    }

    @Test
    fun `when map use case dto to domain then has match result`() {
        val dto = buildUpdateTransactionDTO()

        val result = dto.toDomainDto()

        result.name shouldBe dto.name
        result.amount shouldBe dto.amount
        result.type shouldBe dto.type
        result.direction shouldBe dto.direction
        result.description shouldBe dto.description
        result.created shouldBe dto.created
    }
}
