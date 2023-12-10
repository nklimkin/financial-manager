package me.nikitaklimkin.impl

import arrow.core.Either
import arrow.core.flatMap
import me.nikitaklimkin.*
import me.nikitaklimkin.access.UserPersistence
import java.util.*

class AddNewUserUseCase(private val userPersistence: UserPersistence) : AddNewUser {

    override fun executeBySimpleInfo(request: AddSimpleUserRequest): Either<AddNewUserUseCaseError, Unit> {
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