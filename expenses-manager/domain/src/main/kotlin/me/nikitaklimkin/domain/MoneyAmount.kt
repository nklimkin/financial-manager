package me.nikitaklimkin.domain

import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject
import java.math.BigDecimal

data class MoneyAmount internal constructor(val value: BigDecimal) : ValueObject {

    companion object {

        fun from(amount: BigDecimal): MoneyAmount {
            return MoneyAmount(amount)
        }
    }

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