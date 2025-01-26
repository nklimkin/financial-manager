package me.nikitaklimkin.useCase.account.impl

import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.useCase.account.RemoveAccountError
import me.nikitaklimkin.useCase.account.RemoveAccountRequestDto
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.user.access.UserExtractor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RemoveAccountUseCaseTest {

    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val accountExtractor = mockk<AccountExtractor>(relaxed = true)
    private val accountPersistence = mockk<AccountPersistence>(relaxed = true)

    private lateinit var removeAccountUseCase: RemoveAccountUseCase

    @BeforeEach
    fun setUp() {
        removeAccountUseCase = RemoveAccountUseCase(
            userExtractor,
            accountExtractor,
            accountPersistence
        )
    }

    @Test
    fun `when remove existed account then save disabled account`() {
        val user = buildUser(id = USER_ID)
        val account = listOf(
            depositAccount(id = ACCOUNT_ID),
            cardAccount(id = ACCOUNT_ID_2)
        )
        every { userExtractor.findByUserId(USER_ID) } returns user
        every { accountExtractor.findByUser(user) } returns account
        every { accountPersistence.update(any()) } returns Unit.right()

        val result = removeAccountUseCase.execute(RemoveAccountRequestDto(USER_ID, ACCOUNT_ID))

        result.isRight() shouldBe true
        verify {
            accountPersistence.update(withArg {
                it.id shouldBe ACCOUNT_ID
                it.active shouldBe false
            })
        }
    }

    @Test
    fun `when remove account for not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = removeAccountUseCase.execute(RemoveAccountRequestDto(USER_ID, ACCOUNT_ID))

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe RemoveAccountError.UserNotFound
    }

    @Test
    fun `when remove not existed account then has error`() {
        val user = buildUser(id = USER_ID)
        val account = listOf(
            depositAccount(id = ACCOUNT_ID),
            cardAccount(id = ACCOUNT_ID_2)
        )
        every { userExtractor.findByUserId(USER_ID) } returns user
        every { accountExtractor.findByUser(user) } returns account

        val result = removeAccountUseCase.execute(RemoveAccountRequestDto(USER_ID, ACCOUNT_ID_3))

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe RemoveAccountError.AccountNotFound
    }
}