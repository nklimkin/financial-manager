package me.nikitaklimkin.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.AggregateRoot
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject
import java.time.OffsetDateTime
import java.util.*

data class ExpensesId(private val value: UUID) : ValueObject {
    fun toUuid() = value
}

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

class Expenses internal constructor(
    val id: ExpensesId,
    val amount: Amount,
    val type: String,
    val description: String?,
    val userId: UserId,
    val created: OffsetDateTime
) : AggregateRoot<ExpensesId>(id) {

    companion object {

        fun buildNew(
            id: ExpensesId,
            amount: Amount,
            type: String,
            description: String?,
            userId: UserId
        ): Either<CreateExpensesError, Expenses> {
            return if (type.isBlank()) {
                CreateExpensesError.EmptyTypeError.left()
            } else {
                Expenses(
                    id = id,
                    amount = amount,
                    type = type,
                    description = description,
                    userId = userId,
                    created = OffsetDateTime.now()
                ).right()
            }
        }

        fun build(
            id: ExpensesId,
            amount: Amount,
            type: String,
            description: String?,
            userId: UserId,
            created: OffsetDateTime
        ): Either<CreateExpensesError, Expenses> {
            return if (type.isBlank()) {
                CreateExpensesError.EmptyTypeError.left()
            } else {
                Expenses(
                    id,
                    amount,
                    type,
                    description,
                    userId,
                    created
                ).right()
            }
        }
    }
}

sealed class CreateExpensesError : DomainError() {

    object EmptyTypeError : CreateExpensesError()

}