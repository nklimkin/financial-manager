package me.nikitaklimkin.domain.expenses.dto

import me.nikitaklimkin.domain.expenses.Amount
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.ExpensesName
import me.nikitaklimkin.domain.expenses.ExpensesType
import java.time.OffsetDateTime

data class ExpensesDto(
    val id: ExpensesId,
    val name: ExpensesName,
    val amount: Amount,
    val type: ExpensesType,
    val description: String?,
    val created: OffsetDateTime
)

data class SaveExpensesDto(
    val name: ExpensesName,
    val amount: Amount,
    val type: ExpensesType,
    val description: String?,
    val created: OffsetDateTime
)

data class UpdateExpensesDto(
    val id: ExpensesId,
    val name: ExpensesName?,
    val amount: Amount?,
    val type: ExpensesType?,
    val description: String?,
    val created: OffsetDateTime?
)