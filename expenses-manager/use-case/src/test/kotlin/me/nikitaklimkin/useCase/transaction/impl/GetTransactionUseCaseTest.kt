package me.nikitaklimkin.useCase.transaction.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.transaction.GetTransactionsDTO
import me.nikitaklimkin.useCase.transaction.GetTransactionsError
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.transaction.impl.GetTransactionsUseCase
import me.nikitaklimkin.useCase.user.access.UserExtractor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class GetTransactionUseCaseTest {

    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val accountExtractor = mockk<AccountExtractor>(relaxed = true)
    private val transactionExtractor = mockk<TransactionExtractor>(relaxed = true)

    private lateinit var getTransactionUseCase: GetTransactionsUseCase

    @BeforeEach
    fun setUp() {
        getTransactionUseCase = GetTransactionsUseCase(userExtractor, accountExtractor, transactionExtractor)
    }

    @Test
    fun `when get transaction for existed user and account then has success`() {
        val user = buildUser()
        every { userExtractor.findByUserId(USER_ID) } returns user
        val account = depositAccount()
        every { accountExtractor.findByUser(user) } returns listOf(account, cardAccount(id = ACCOUNT_ID_2))
        every { transactionExtractor.findByAccount(account) } returns
                mutableListOf(
                    buildTransaction(id = TransactionId(UUID.randomUUID())),
                    buildTransaction(id = TransactionId(UUID.randomUUID()))
                )

        val result = getTransactionUseCase.execute(GetTransactionsDTO(USER_ID, ACCOUNT_ID))

        result.isRight() shouldBe true
        val resultPayload = result.getOrNull()!!
        resultPayload.size shouldBe 2
    }

    @Test
    fun `when get transaction for not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = getTransactionUseCase.execute(GetTransactionsDTO(USER_ID, ACCOUNT_ID))

        result.isLeft() shouldBe true
        result.leftOrNull()!! shouldBe GetTransactionsError.TransactionsNotFound
    }

    @Test
    fun `when get transaction for not existed account then has error`() {
        val user = buildUser()
        every { userExtractor.findByUserId(USER_ID) } returns user
        every { accountExtractor.findByUser(user) } returns listOf()

        val result = getTransactionUseCase.execute(GetTransactionsDTO(USER_ID, ACCOUNT_ID))

        result.isLeft() shouldBe true
        result.leftOrNull()!! shouldBe GetTransactionsError.TransactionsNotFound
    }

    @Test
    fun `when get zero transaction for existed user and account then has success`() {
        val user = buildUser()
        every { userExtractor.findByUserId(USER_ID) } returns user
        val account = depositAccount()
        every { accountExtractor.findByUser(user) } returns listOf(account, cardAccount(id = ACCOUNT_ID_2))
        every { transactionExtractor.findByAccount(account) } returns listOf()

        val result = getTransactionUseCase.execute(GetTransactionsDTO(USER_ID, ACCOUNT_ID))

        result.isRight() shouldBe true
        val resultPayload = result.getOrNull()!!
        resultPayload.size shouldBe 0
    }
}