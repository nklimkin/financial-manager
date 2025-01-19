package me.nikitaklimkin.useCase.user.impl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.VALID_USER_NAME
import me.nikitaklimkin.domain.buildUser
import me.nikitaklimkin.useCase.buildValidAddSimpleUserRequest
import me.nikitaklimkin.useCase.user.access.UserExtractor
import me.nikitaklimkin.useCase.user.access.UserPersistence
import org.junit.jupiter.api.Test

class AddNewUserUseCaseTest {

    private val userPersistence = mockk<UserPersistence>(relaxed = true)
    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val addNewUserUseCase = AddNewUserUseCase(userPersistence, userExtractor)

    @Test
    fun `when process valid request when has match result`() {
        every { userExtractor.findByUserName(any()) } returns null

        val request = buildValidAddSimpleUserRequest()

        val executedResult = addNewUserUseCase.executeBySimpleInfo(request)

        executedResult.isRight() shouldBe true
        verify {
            userPersistence.save(withArg {
                it.userName().getValue() shouldBe VALID_USER_NAME
                it.active() shouldBe true
            })
        }
    }

    @Test
    fun `when process simple request with already existed user then throw exception`() {
        every { userExtractor.findByUserName(any()) } returns buildUser()

        val request = buildValidAddSimpleUserRequest()

        val executedResult = addNewUserUseCase.executeBySimpleInfo(request)

        executedResult.isLeft() shouldBe true
        executedResult.leftOrNull() shouldNotBe null
    }
}