package me.nikitaklimkin

import arrow.core.Either
import me.nikitaklimkin.model.DomainError
import kotlinx.serialization.Serializable

interface AddNewUser {

    fun executeBySimpleInfo(request: AddSimpleUserRequest): Either<AddNewUserUseCaseError, Unit>

    fun executeByTelegramInfo(request: AddTelegramUserRequest): Either<AddNewUserUseCaseError, Unit>
}

@Serializable
data class AddSimpleUserRequest(val userName: String)

@Serializable
data class AddTelegramUserRequest(
    val chatId: Long,
    val userName: String
)

class AddNewUserUseCaseError : DomainError()