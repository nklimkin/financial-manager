package me.nikitaklimkin.useCase.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import me.nikitaklimkin.domain.user.CreateUserError
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.user.UserName
import me.nikitaklimkin.useCase.AddNewUser
import me.nikitaklimkin.useCase.AddNewUserUseCaseError
import me.nikitaklimkin.useCase.AddSimpleUserRequest
import me.nikitaklimkin.useCase.AddTelegramUserRequest
import me.nikitaklimkin.useCase.access.UserExtractor
import me.nikitaklimkin.useCase.access.UserPersistence
import mu.KotlinLogging
import java.util.*

private val log = KotlinLogging.logger {}

class AddNewUserUseCase(
    private val userPersistence: UserPersistence,
    private val userExtractor: UserExtractor
) : AddNewUser {

    override fun executeBySimpleInfo(request: AddSimpleUserRequest): Either<AddNewUserUseCaseError, Unit> {
        log.debug { "Execute add new user with userName = ${request.userName}" }
        log.trace { "Execute add new user with request = $request" }
        val persistedUser = userExtractor.findByUserName(request.userName)
        if (persistedUser.isRight()) {
            log.error { "User with name ${request.userName} already exists" }
            return AddNewUserUseCaseError().left()
        }
        return UserName.create(request.userName)
            .flatMap {
                User.buildNew(
                    UserId(UUID.randomUUID()),
                    it
                )
            }
            .mapLeft { it.toError() }
            .map { userPersistence.save(it) }
    }

    override fun executeByTelegramInfo(request: AddTelegramUserRequest): Either<AddNewUserUseCaseError, Unit> {
        log.debug { "Execute add new user with telegram chatId = ${request.chatId}" }
        log.trace { "Execute add new user with telegram info with request = $request" }
        val persistedUser = userExtractor.findByTelegramChatId(request.chatId)
        if (persistedUser.isRight()) {
            log.error { "User with chatId ${request.chatId} already exists" }
            return AddNewUserUseCaseError().left()
        }
        return UserName.create(request.userName)
            .flatMap {
                User.buildNewByTelegram(
                    UserId(UUID.randomUUID()),
                    request.chatId,
                    it
                )
            }
            .mapLeft { it.toError() }
            .map { userPersistence.save(it) }
    }
}

fun CreateUserError.toError() = AddNewUserUseCaseError()