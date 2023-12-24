package me.nikitaklimkin.impl

import TELEGRAM_CHAT_ID
import VALID_USER_NAME
import arrow.core.left
import arrow.core.right
import buildInvalidAddSimpleUserRequest
import buildInvalidAddTelegramUserRequest
import buildUser
import buildValidAddSimpleUserRequest
import buildValidAddTelegramUserRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.AddNewUserUseCaseError
import me.nikitaklimkin.access.UserExtractor
import me.nikitaklimkin.access.UserNotFound
import me.nikitaklimkin.access.UserPersistence
import org.junit.jupiter.api.Test

class AddNewUserUseCaseTest {

    private val userPersistence = mockk<UserPersistence>(relaxed = true)
    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val addNewUserUseCase = AddNewUserUseCase(userPersistence, userExtractor)

    @Test
    fun `when process valid request when has match result`() {
        every { userExtractor.findByUserName(any()) } returns UserNotFound().left()

        val request = buildValidAddSimpleUserRequest()

        val executedResult = addNewUserUseCase.executeBySimpleInfo(request)

        executedResult.isRight() shouldBe true
        verify {
            userPersistence.save(withArg {
                it.userName()?.getValue() shouldBe VALID_USER_NAME
                it.active() shouldBe true
                it.telegramUser() shouldBe null
            })
        }
    }

    @Test
    fun `when process invalid request when has match result`() {
        every { userExtractor.findByUserName(any()) } returns UserNotFound().left()

        val request = buildInvalidAddSimpleUserRequest()

        val executedResult = addNewUserUseCase.executeBySimpleInfo(request)

        executedResult.isLeft() shouldBe true
        verify(exactly = 0) { userPersistence.save(any()) }
    }

    @Test
    fun `when process simple request with already existed user then throw exception`() {
        every { userExtractor.findByUserName(any()) } returns buildUser().right()

        val request = buildValidAddSimpleUserRequest()

        val executedResult = addNewUserUseCase.executeBySimpleInfo(request)

        executedResult.isLeft() shouldBe true
        executedResult.leftOrNull() shouldNotBe null
    }

    @Test
    fun `when process valid request with telegram info when has match result`() {
        every { userExtractor.findByTelegramChatId(any()) } returns UserNotFound().left()

        val request = buildValidAddTelegramUserRequest()

        val executedResult = addNewUserUseCase.executeByTelegramInfo(request)

        executedResult.isRight() shouldBe true
        verify {
            userPersistence.save(withArg {
                it.userName()?.getValue() shouldBe null
                it.active() shouldBe true
                it.telegramUser() shouldNotBe null
                it.telegramUser()?.chatId shouldBe TELEGRAM_CHAT_ID
                it.telegramUser()?.userName?.getValue() shouldBe VALID_USER_NAME
            })
        }
    }

    @Test
    fun `when process invalid request with telegram info when has match result`() {
        every { userExtractor.findByTelegramChatId(any()) } returns UserNotFound().left()

        val request = buildInvalidAddTelegramUserRequest()

        val executedResult = addNewUserUseCase.executeByTelegramInfo(request)

        executedResult.isLeft() shouldBe true
        verify(exactly = 0) { userPersistence.save(any()) }
    }

    @Test
    fun `when process telegram request with already existed user then throw exception`() {
        every { userExtractor.findByTelegramChatId(any()) } returns buildUser().right()

        val request = buildValidAddTelegramUserRequest()

        val executedResult = addNewUserUseCase.executeByTelegramInfo(request)

        executedResult.isLeft() shouldBe true
        executedResult.leftOrNull() shouldNotBe null
    }
}