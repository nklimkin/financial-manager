package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class BankName(val value: String) : ValueObject {

    companion object {

        fun from(value: String): Either<BankNameCreateError, BankName> {
            return if (value.isBlank()) {
                BankNameCreateError.left()
            } else {
                BankName(value).right()
            }
        }

    }
}

object BankNameCreateError : DomainError()