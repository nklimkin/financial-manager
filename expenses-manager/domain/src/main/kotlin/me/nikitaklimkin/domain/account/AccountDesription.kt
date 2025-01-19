package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class AccountDescription(val value: String) : ValueObject {

    companion object {

        fun from(value: String): Either<AccountDescriptionCreateError, AccountDescription> {
            return if (value.isBlank()) {
                AccountDescriptionCreateError.left()
            } else {
                AccountDescription(value).right()
            }
        }

    }

}

object AccountDescriptionCreateError : DomainError()