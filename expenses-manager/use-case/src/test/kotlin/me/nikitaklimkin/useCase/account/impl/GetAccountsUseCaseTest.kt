package me.nikitaklimkin.useCase.account.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.useCase.account.GetAccountError
import me.nikitaklimkin.useCase.account.GetAccountRequest
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.user.access.UserExtractor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAccountsUseCaseTest {

    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val accountExtractor = mockk<AccountExtractor>(relaxed = true)

    private lateinit var getAccountUseCase: GetAccountsUseCase

    @BeforeEach
    fun setUp() {
        getAccountUseCase = GetAccountsUseCase(
            userExtractor,
            accountExtractor
        )
    }

    @Test
    fun `when get existed accounts then has match result`() {
        val user = buildUser(id = USER_ID)
        val accounts = listOf(
            depositAccount(),
            cardAccount(),
            brokerAccount(),
            piggyAccount()
        )
        every { userExtractor.findByUserId(USER_ID) } returns user
        every { accountExtractor.findByUser(user) } returns accounts

        val result = getAccountUseCase.execute(GetAccountRequest(USER_ID))

        result.isRight() shouldBe true
        result.getOrNull()!!.userId shouldBe USER_ID
        result.getOrNull()!!.accounts.size shouldBe 4
    }

    @Test
    fun `when get accounts for not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = getAccountUseCase.execute(GetAccountRequest(USER_ID))

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe GetAccountError.UserNotFound
    }

    @Test
    fun `whe there is no accounts for user then return empty list`() {
        val user = buildUser(id = USER_ID)
        every { userExtractor.findByUserId(USER_ID) } returns user
        every { accountExtractor.findByUser(user) } returns listOf()

        val result = getAccountUseCase.execute(GetAccountRequest(USER_ID))

        result.isRight() shouldBe true
        result.getOrNull()!!.userId shouldBe USER_ID
        result.getOrNull()!!.accounts.size shouldBe 0
    }
}