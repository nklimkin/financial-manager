package me.nikitaklimkin.useCase.user.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.user.UserIdGenerator
import me.nikitaklimkin.useCase.user.UserLogin
import me.nikitaklimkin.useCase.user.UserLoginError
import me.nikitaklimkin.useCase.user.OauthLoginUserRequest
import me.nikitaklimkin.useCase.user.access.UserExtractor
import me.nikitaklimkin.useCase.user.access.UserPersistence
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class UserLoginUseCase(
    private val userPersistence: UserPersistence,
    private val userExtractor: UserExtractor,
    private val userIdGenerator: UserIdGenerator
) : UserLogin {

    override fun loginByOauth(request: OauthLoginUserRequest): Either<UserLoginError, UserId> {
        log.debug { "Execute login user with userName = ${request.userName} and oauth id = ${request.oauthId}" }
        log.trace { "Execute login user with request = $request" }
        val persistedUser = userExtractor.findByOauthId(request.oauthId)
        if (persistedUser != null) {
            return persistedUser.id.right()
        }
        return register(request)
    }

    private fun register(request: OauthLoginUserRequest): Either<UserLoginError, UserId> {
        log.debug { "Register user with oauthId = [${request.oauthId}] and userName = [${request.userName}]" }
        return User.build(
            userIdGenerator,
            request.oauthId,
            request.userName
        )
            .mapLeft { UserLoginError() }
            .flatMap { user ->
                userPersistence.save(user)
                    .mapLeft { UserLoginError() }
                    .map { user.id }
            }
    }
}