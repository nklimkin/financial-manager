package me.nikitaklimkin.domain.expenses

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class Amount internal constructor(private val amount: Double) : ValueObject {

    companion object {

        fun from(amount: Double): Either<NotValidPriceError, Amount> {
            return if (amount < 0) {
                NotValidPriceError.left()
            } else {
                Amount(amount).right()
            }
        }
    }

    fun toDoubleValue() = amount

}

object NotValidPriceError : DomainError()