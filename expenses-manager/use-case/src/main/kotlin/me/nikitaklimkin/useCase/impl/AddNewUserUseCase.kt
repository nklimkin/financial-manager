package me.nikitaklimkin.useCase.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import me.nikitaklimkin.domain.CreateUserError
import me.nikitaklimkin.domain.User
import me.nikitaklimkin.domain.UserId
import me.nikitaklimkin.domain.UserName
import me.nikitaklimkin.useCase.AddNewUser
import me.nikitaklimkin.useCase.AddNewUserUseCaseError
import me.nikitaklimkin.useCase.AddSimpleUserRequest
import me.nikitaklimkin.useCase.AddTelegramUserRequest
import me.nikitaklimkin.useCase.access.UserExtractor
import me.nikitaklimkin.useCase.access.UserPersistence
import java.util.*

class AddNewUserUseCase(
    private val userPersistence: UserPersistence,
    private val userExtractor: UserExtractor
) : AddNewUser {

    override fun executeBySimpleInfo(request: AddSimpleUserRequest): Either<AddNewUserUseCaseError, Unit> {
        val persistedUser = userExtractor.findByUserName(request.userName)
        if (persistedUser.isRight()) {
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
        val persistedUser = userExtractor.findByTelegramChatId(request.chatId)
        if (persistedUser.isRight()) {
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