package me.nikitaklimkin.useCase.user.access

import arrow.core.Either
import me.nikitaklimkin.domain.user.User

interface UserPersistence {

    fun save(user: User): Either<UserPersistenceError.UserAlreadyExists, Unit>

    fun update(user: User): Either<UserPersistenceError.UserNotFound, Unit>

}

sealed class UserPersistenceError {

    data object UserAlreadyExists : UserPersistenceError()

    data object UserNotFound : UserPersistenceError()

}