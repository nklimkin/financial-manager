package me.nikitaklimkin.domain.expenses

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.model.DomainEntity
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject
import java.time.OffsetDateTime
import java.util.*

data class ExpensesId(private val value: UUID) : ValueObject {

    companion object {

        fun from(value: String): Either<InvalidExpensesIdError, ExpensesId> {
            return try {
                ExpensesId(UUID.fromString(value)).right()
            } catch (exception: IllegalArgumentException) {
                return InvalidExpensesIdError.left()
            }
        }

    }

    fun toUuid() = value
    override fun toString() = value.toString()
}

object InvalidExpensesIdError : DomainError()

class Expenses(
    val id: ExpensesId,
    var name: ExpensesName,
    var amount: Amount,
    var type: ExpensesType,
    var description: String?,
    var created: OffsetDateTime
) : DomainEntity<ExpensesId>(id) {

    fun toDto(): ExpensesDto {
        return ExpensesDto(
            id,
            name,
            amount,
            type,
            description,
            created
        )
    }
}