package me.nikitaklimkin.useCase.user.impl

import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.USER_OAUTH_ID
import me.nikitaklimkin.domain.VALID_USER_NAME
import me.nikitaklimkin.domain.buildUser
import me.nikitaklimkin.domain.user.UserIdGenerator
import me.nikitaklimkin.useCase.buildValidOauthLoginUserRequest
import me.nikitaklimkin.useCase.user.access.UserExtractor
import me.nikitaklimkin.useCase.user.access.UserPersistence
import org.junit.jupiter.api.Test

class UserLoginUseCaseTest {

    private val userPersistence = mockk<UserPersistence>(relaxed = true)
    private val userExtractor = mockk<UserExtractor>(relaxed = true)
    private val userIdGenerator = mockk<UserIdGenerator>(relaxed = true)
    private val loginUserUseCase = UserLoginUseCase(userPersistence, userExtractor, userIdGenerator)

    @Test
    fun `when process request for not existed user then save new info`() {
        every { userIdGenerator.generate() } returns USER_ID
        every { userExtractor.findByOauthId(any()) } returns null
        every { userPersistence.save(any()) } returns Unit.right()

        val request = buildValidOauthLoginUserRequest()

        val executedResult = loginUserUseCase.loginByOauth(request)

        executedResult.isRight() shouldBe true
        executedResult.getOrNull() shouldBe USER_ID
        verify {
            userPersistence.save(withArg {
                it.userName().getValue() shouldBe VALID_USER_NAME
                it.oauthId shouldBe USER_OAUTH_ID
                it.active() shouldBe true
            })
        }
    }

    @Test
    fun `when process request for existed user then return it`() {
        every { userExtractor.findByOauthId(any()) } returns buildUser(id = USER_ID)

        val request = buildValidOauthLoginUserRequest()

        val executedResult = loginUserUseCase.loginByOauth(request)

        executedResult.isRight() shouldBe true
        executedResult.getOrNull() shouldBe USER_ID
    }
}