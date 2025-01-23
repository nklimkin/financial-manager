package me.nikitaklimkin.useCase.transaction.impl

import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.transaction.DeleteTransactionDTO
import me.nikitaklimkin.useCase.transaction.RemoveTransactionError
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import me.nikitaklimkin.useCase.transaction.impl.RemoveTransactionUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RemoveTransactionUseCaseTest {

    private val accountExtractor = mockk<AccountExtractor>()
    private val transactionExtractor = mockk<TransactionExtractor>()
    private val transactionPersistence = mockk<TransactionPersistence>()

    private lateinit var removeTransactionUseCase: RemoveTransactionUseCase

    @BeforeEach
    fun setUp() {
        removeTransactionUseCase =
            RemoveTransactionUseCase(accountExtractor, transactionExtractor, transactionPersistence)
    }

    @Test
    fun `when delete existed transaction then has success`() {
        every { accountExtractor.findById(ACCOUNT_ID) } returns piggyAccount()
        every { transactionExtractor.findById(TRANSACTION_ID) } returns buildTransaction()
        every { transactionPersistence.update(any()) } returns Unit.right()

        val result = removeTransactionUseCase.execute(DeleteTransactionDTO(USER_ID, TRANSACTION_ID))

        result.isRight() shouldBe true
        verify { transactionPersistence.update(withArg { it.active shouldBe false }) }
    }

    @Test
    fun `when delete not existed transaction then has error`() {
        every { transactionExtractor.findById(TRANSACTION_ID) } returns null

        val result = removeTransactionUseCase.execute(DeleteTransactionDTO(USER_ID, TRANSACTION_ID))

        result.isLeft() shouldBe true
        result.leftOrNull()!! shouldBe RemoveTransactionError.TransactionNotFound
    }

    @Test
    fun `when delete existed transaction but for another user then has error`() {
        every { accountExtractor.findById(ACCOUNT_ID) } returns piggyAccount()
        every { transactionExtractor.findById(TRANSACTION_ID) } returns buildTransaction()

        val result = removeTransactionUseCase.execute(DeleteTransactionDTO(USER_ID_2, TRANSACTION_ID))

        result.isLeft() shouldBe true
        result.leftOrNull()!! shouldBe RemoveTransactionError.TransactionNotFound
    }
}