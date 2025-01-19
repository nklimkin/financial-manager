package me.nikitaklimkin.domain

import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class MoneyAmount internal constructor(private val value: Double) : ValueObject {

    companion object {

        fun from(amount: Double): MoneyAmount {
            return MoneyAmount(amount)
        }
    }

    fun toDoubleValue() = value

    operator fun plus(amount: MoneyAmount): MoneyAmount {
        return MoneyAmount(value + amount.value)
    }

    operator fun minus(amount: MoneyAmount): MoneyAmount {
        return MoneyAmount(value - amount.value)
    }

    operator fun compareTo(amount: MoneyAmount): Int {
        return value.compareTo(amount.value)
    }

}

object NotValidMoneyAmountError : DomainError()