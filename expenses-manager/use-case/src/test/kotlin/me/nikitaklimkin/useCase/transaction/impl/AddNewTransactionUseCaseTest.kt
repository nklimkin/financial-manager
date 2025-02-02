package me.nikitaklimkin.useCase.transaction.impl

import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.buildAddNewTransactionDTO
import me.nikitaklimkin.useCase.transaction.AddNewTransactionError
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import me.nikitaklimkin.useCase.transaction.impl.AddNewTransactionUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddNewTransactionUseCaseTest {

    private val transactionPersistence = mockk<TransactionPersistence>(relaxed = true)
    private val transactionIdGenerator = TransactionIdGeneratorFixtures()
    private val accountExtractor = mockk<AccountExtractor>(relaxed = true)

    private lateinit var addNewTransactionUseCase: AddNewTransactionUseCase

    @BeforeEach
    fun setUp() {
        addNewTransactionUseCase = AddNewTransactionUseCase(
            transactionPersistence,
            transactionIdGenerator,
            accountExtractor
        )
    }

    @Test
    fun `when add transaction for existed account then has success`() {
        every { accountExtractor.findById(ACCOUNT_ID) } returns depositAccount()
        every { transactionPersistence.save(any()) } returns Unit.right()

        val result = addNewTransactionUseCase.execute(buildAddNewTransactionDTO())

        result.isRight() shouldBe true
    }

    @Test
    fun `when add transaction for existed account then persists it`() {
        every { accountExtractor.findById(ACCOUNT_ID) } returns depositAccount()

        addNewTransactionUseCase.execute(buildAddNewTransactionDTO())

        verify {
            transactionIdGenerator.generate()
            transactionPersistence.save(withArg {
                it.id shouldBe TRANSACTION_ID
            })
        }
    }

    @Test
    fun `when add transaction for not existed account then has error`() {
        every { accountExtractor.findById(ACCOUNT_ID) } returns null

        val result = addNewTransactionUseCase.execute(buildAddNewTransactionDTO())

        result.isLeft() shouldBe true
    }

    @Test
    fun `when add transaction for account of other user then has error`() {
        every { accountExtractor.findById(ACCOUNT_ID) } returns depositAccount()

        val result = addNewTransactionUseCase.execute(
            buildAddNewTransactionDTO(userId = USER_ID_2)
        )

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewTransactionError.AccountNotFound
    }
}