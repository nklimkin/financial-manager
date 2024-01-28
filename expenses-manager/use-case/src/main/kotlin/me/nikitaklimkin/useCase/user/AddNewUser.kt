package me.nikitaklimkin.useCase.user

import arrow.core.Either
import me.nikitaklimkin.model.DomainError

interface AddNewUser {

    fun executeBySimpleInfo(request: AddSimpleUserRequest): Either<AddNewUserUseCaseError, Unit>

    fun executeByTelegramInfo(request: AddTelegramUserRequest): Either<AddNewUserUseCaseError, Unit>
}

data class AddSimpleUserRequest(val userName: String)

data class AddTelegramUserRequest(
    val chatId: Long,
    val userName: String
)

class AddNewUserUseCaseError : DomainError()