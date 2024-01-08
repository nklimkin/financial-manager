package me.nikitaklimkin.domain.expenses

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class ExpensesName internal constructor(val name: String) : ValueObject {

    companion object {

        fun from(name: String): Either<NotValidExpensesNameError, ExpensesName> {
            return if (name.isBlank()) {
                NotValidExpensesNameError.left()
            } else {
                ExpensesName(name).right()
            }
        }

    }

    fun toStringValue() = name

}

object NotValidExpensesNameError : DomainError()
