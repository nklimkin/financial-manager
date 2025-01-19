package me.nikitaklimkin.domain.transaction

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class TransactionName internal constructor(val name: String) : ValueObject {

    companion object {

        fun from(name: String): Either<NotValidTransactionNameError, TransactionName> {
            return if (name.isBlank()) {
                NotValidTransactionNameError.left()
            } else {
                TransactionName(name).right()
            }
        }

    }

    fun toStringValue() = name

}

object NotValidTransactionNameError : DomainError()
