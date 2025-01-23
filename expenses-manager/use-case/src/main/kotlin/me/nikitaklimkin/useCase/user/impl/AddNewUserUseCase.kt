package me.nikitaklimkin.useCase.user.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.domain.user.UserIdGenerator
import me.nikitaklimkin.useCase.user.AddNewUser
import me.nikitaklimkin.useCase.user.AddNewUserUseCaseError
import me.nikitaklimkin.useCase.user.AddSimpleUserRequest
import me.nikitaklimkin.useCase.user.access.UserExtractor
import me.nikitaklimkin.useCase.user.access.UserPersistence
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class AddNewUserUseCase(
    private val userPersistence: UserPersistence,
    private val userExtractor: UserExtractor,
    private val userIdGenerator: UserIdGenerator
) : AddNewUser {

    override fun executeBySimpleInfo(request: AddSimpleUserRequest): Either<AddNewUserUseCaseError, Unit> {
        log.debug { "Execute add new user with userName = ${request.userName}" }
        log.trace { "Execute add new user with request = $request" }
        val persistedUser = userExtractor.findByUserName(request.userName)
        if (persistedUser != null) {
            log.error { "User with name ${request.userName} already exists" }
            return AddNewUserUseCaseError().left()
        }
        return User.build(
            userIdGenerator,
            request.userName
        )
            .mapLeft { _ -> AddNewUserUseCaseError() }
            .flatMap {
                userPersistence.save(it)
                    .mapLeft { _ -> AddNewUserUseCaseError() }
            }
    }
}