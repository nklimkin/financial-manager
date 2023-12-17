package me.nikitaklimkin.impl

import TELEGRAM_CHAT_ID
import VALID_USER_NAME
import buildInvalidAddSimpleUserRequest
import buildInvalidAddTelegramUserRequest
import buildValidAddSimpleUserRequest
import buildValidAddTelegramUserRequest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.access.UserPersistence
import org.junit.jupiter.api.Test

class AddNewUserUseCaseTest {

    private val userPersistence = mockk<UserPersistence>(relaxed = true)
    private val addNewUserUseCase = AddNewUserUseCase(userPersistence)

    @Test
    fun `when process valid request when has match result`() {
        val request = buildValidAddSimpleUserRequest()

        val executedResult = addNewUserUseCase.executeBySimpleInfo(request)

        executedResult.isRight()
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
        val request = buildInvalidAddSimpleUserRequest()

        val executedResult = addNewUserUseCase.executeBySimpleInfo(request)

        executedResult.isLeft()
        verify(exactly = 0) { userPersistence.save(any()) }
    }

    @Test
    fun `when process valid request with telegram info when has match result`() {
        val request = buildValidAddTelegramUserRequest()

        val executedResult = addNewUserUseCase.executeByTelegramInfo(request)

        executedResult.isRight()
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
        val request = buildInvalidAddTelegramUserRequest()

        val executedResult = addNewUserUseCase.executeByTelegramInfo(request)

        executedResult.isLeft()
        verify(exactly = 0) { userPersistence.save(any()) }
    }
}