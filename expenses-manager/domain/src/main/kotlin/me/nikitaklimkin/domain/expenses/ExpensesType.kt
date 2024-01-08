package me.nikitaklimkin.domain.expenses

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class ExpensesType internal constructor(private val type: String) : ValueObject {

    companion object {

        fun from(type: String): Either<NotValidExpensesType, ExpensesType> {
            if (type.isBlank()) {
                return NotValidExpensesType.left()
            }
            return ExpensesType(type).right()
        }

    }

    fun toStringValue() = type.toString()

}

object NotValidExpensesType : DomainError()