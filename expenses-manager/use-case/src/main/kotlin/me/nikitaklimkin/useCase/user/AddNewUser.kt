package me.nikitaklimkin.useCase.user

import arrow.core.Either
import me.nikitaklimkin.domain.user.UserName
import me.nikitaklimkin.model.DomainError

interface AddNewUser {

    fun executeBySimpleInfo(request: AddSimpleUserRequest): Either<AddNewUserUseCaseError, Unit>
}

data class AddSimpleUserRequest(val userName: UserName)

class AddNewUserUseCaseError : DomainError()