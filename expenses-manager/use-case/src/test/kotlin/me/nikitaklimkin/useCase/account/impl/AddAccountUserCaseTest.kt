package me.nikitaklimkin.useCase.account.impl

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.ACCOUNT_ID
import me.nikitaklimkin.domain.FixturesAccountIdGenerator
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.buildUser
import me.nikitaklimkin.useCase.account.AddNewAccountError
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.account.access.AccountPersistenceError
import me.nikitaklimkin.useCase.buildAddBrokerAccountRequest
import me.nikitaklimkin.useCase.buildAddCardAccountRequest
import me.nikitaklimkin.useCase.buildAddDepositAccountRequest
import me.nikitaklimkin.useCase.buildAddPiggyAccountRequest
import me.nikitaklimkin.useCase.user.access.UserExtractor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddAccountUserCaseTest {

    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val accountPersistence = mockk<AccountPersistence>(relaxed = true)
    private val accountIdGenerator = FixturesAccountIdGenerator()

    private lateinit var addAccountUseCase: AddAccountUseCase

    @BeforeEach
    fun setUp() {
        addAccountUseCase = AddAccountUseCase(
            userExtractor,
            accountPersistence,
            accountIdGenerator
        )
    }

    @Test
    fun `when add broker account then save it and has match result`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns Unit.right()

        val result = addAccountUseCase.addBrokerAccount(buildAddBrokerAccountRequest())

        result.isRight() shouldBe true
        verify {
            accountPersistence.save(withArg {
                it.id shouldBe ACCOUNT_ID
            })
        }
    }

    @Test
    fun `when add broker account for not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = addAccountUseCase.addBrokerAccount(buildAddBrokerAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.UserNotFound
    }

    @Test
    fun `when save duplicate broker account then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns AccountPersistenceError.AccountAlreadyExists.left()

        val result = addAccountUseCase.addBrokerAccount(buildAddBrokerAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.AccountAlreadyExists
    }

    @Test
    fun `when add card account then save it and has match result`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns Unit.right()

        val result = addAccountUseCase.addCardAccount(buildAddCardAccountRequest())

        result.isRight() shouldBe true
        verify {
            accountPersistence.save(withArg {
                it.id shouldBe ACCOUNT_ID
            })
        }
    }

    @Test
    fun `when add card account for not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = addAccountUseCase.addCardAccount(buildAddCardAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.UserNotFound
    }

    @Test
    fun `when save duplicate card account then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns AccountPersistenceError.AccountAlreadyExists.left()

        val result = addAccountUseCase.addCardAccount(buildAddCardAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.AccountAlreadyExists
    }

    @Test
    fun `when add deposit account then save it and has match result`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns Unit.right()

        val result = addAccountUseCase.addDepositAccount(buildAddDepositAccountRequest())

        result.isRight() shouldBe true
        verify {
            accountPersistence.save(withArg {
                it.id shouldBe ACCOUNT_ID
            })
        }
    }

    @Test
    fun `when add deposit account for not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = addAccountUseCase.addDepositAccount(buildAddDepositAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.UserNotFound
    }

    @Test
    fun `when save duplicate deposit account then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns AccountPersistenceError.AccountAlreadyExists.left()

        val result = addAccountUseCase.addDepositAccount(buildAddDepositAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.AccountAlreadyExists
    }

    @Test
    fun `when add piggy account then save it and has match result`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns Unit.right()

        val result = addAccountUseCase.addPiggyAccount(buildAddPiggyAccountRequest())

        result.isRight() shouldBe true
        verify {
            accountPersistence.save(withArg {
                it.id shouldBe ACCOUNT_ID
            })
        }
    }

    @Test
    fun `when add piggy account for not existed user then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns null

        val result = addAccountUseCase.addPiggyAccount(buildAddPiggyAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.UserNotFound
    }

    @Test
    fun `when save duplicate piggy account then has error`() {
        every { userExtractor.findByUserId(USER_ID) } returns buildUser(id = USER_ID)
        every { accountPersistence.save(any()) } returns AccountPersistenceError.AccountAlreadyExists.left()

        val result = addAccountUseCase.addPiggyAccount(buildAddPiggyAccountRequest())

        result.isLeft() shouldBe true
        result.leftOrNull() shouldBe AddNewAccountError.AccountAlreadyExists
    }
}