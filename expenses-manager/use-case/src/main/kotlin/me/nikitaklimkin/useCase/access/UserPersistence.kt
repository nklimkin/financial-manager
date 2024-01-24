package me.nikitaklimkin.useCase.access

import arrow.core.Either
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.model.DomainError

interface UserPersistence {

    fun save(user: User): Either<DomainError, Unit>

}