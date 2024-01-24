package me.nikitaklimkin.domain.expenses

import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.model.DomainEntity
import me.nikitaklimkin.model.ValueObject
import java.time.OffsetDateTime
import java.util.*

data class ExpensesId(private val value: UUID) : ValueObject {
    fun toUuid() = value
    override fun toString() = value.toString()
}

class Expenses (
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