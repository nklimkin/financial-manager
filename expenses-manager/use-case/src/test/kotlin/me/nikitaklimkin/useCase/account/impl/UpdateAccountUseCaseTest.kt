package me.nikitaklimkin.useCase.account.impl

import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.useCase.account.UpdateAccountError
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.buildUpdateBrokerAccountRequest
import me.nikitaklimkin.useCase.user.access.UserExtractor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateAccountUseCaseTest {

    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val accountExtractor = mockk<AccountExtractor>(relaxed = true)
    private val accountPersistence = mockk<AccountPersistence>(relaxed = true)


    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    @BeforeEach
    fun setUp() {
        updateAccountUseCase = UpdateAccountUseCase(
            userExtractor,
            accountExtractor,
            accountPersistence
        )
    }

    @Test
    fun `when update broker account then has match result`() {
        val user = buildUser(id = USER_ID)
        every { userExtractor.findByUserId(USER_ID) } returns user
        val accounts = listOf(
            brokerAccount(id = ACCOUNT_ID),
            cardAccount(id = ACCOUNT_ID_2),
            piggyAccount(id = ACCOUNT_ID_3),
            depositAccount(id = ACCOUNT_ID_4),
        )
        every { accountExtractor.findByUser(user) } returns accounts
        every { accountPersistence.update(any()) } returns Unit.right()

        val result = updateAccountUseCase.execute(buildUpdateBrokerAccountRequest())

        result.isRight() shouldBe true
        verify {
            accountPersistence.update(withArg {
                it.id shouldBe ACCOUNT_ID
                it.bankName shouldBe TEST_BANK_NAME_2
            })
        }
    }

    @Test
    fun `when update by invalid request then has error`() {
        val user = buildUser(id = USER_ID)
        every { userExtractor.findByUserId(USER_ID) } returns user
        val accounts = listOf(
            brokerAccount(id = ACCOUNT_ID_2),
            cardAccount(id = ACCOUNT_ID),
            piggyAccount(id = ACCOUNT_ID_3),
            depositAccount(id = ACCOUNT_ID_4),
        )
        every { accountExtractor.findByUser(user) } returns accounts
        every { accountPersistence.update(any()) } returns Unit.right()

        val result = updateAccountUseCase.execute(buildUpdateBrokerAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe UpdateAccountError.InvalidRequest
    }

    @Test
    fun `when update by not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = updateAccountUseCase.execute(buildUpdateBrokerAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe UpdateAccountError.UserNotFound
    }

    @Test
    fun `when update by not existed account then has error`() {
        val user = buildUser(id = USER_ID)
        every { userExtractor.findByUserId(USER_ID) } returns user
        val accounts = listOf(
            cardAccount(id = ACCOUNT_ID_2),
            piggyAccount(id = ACCOUNT_ID_3),
            depositAccount(id = ACCOUNT_ID_4),
        )
        every { accountExtractor.findByUser(user) } returns accounts

        val result = updateAccountUseCase.execute(buildUpdateBrokerAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe UpdateAccountError.AccountNotFound
    }
}