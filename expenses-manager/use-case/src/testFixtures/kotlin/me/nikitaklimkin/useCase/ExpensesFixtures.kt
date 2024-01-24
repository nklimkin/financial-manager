package me.nikitaklimkin.useCase

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.expenses.Amount
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.ExpensesName
import me.nikitaklimkin.domain.expenses.ExpensesType
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.useCase.expenses.AddNewExpensesDto
import me.nikitaklimkin.useCase.expenses.UpdateExpensesDto
import me.nikitaklimkin.useCase.expenses.UpdateExpensesError
import java.time.OffsetDateTime

fun buildAddExpenses(
    name: ExpensesName = buildName(),
    amount: Amount = buildAmount(),
    type: ExpensesType = buildType(),
    description: String? = DESCRIPTION,
    created: OffsetDateTime = OffsetDateTime.now(),
    userId: UserId = USER_ID
) = AddNewExpensesDto(name, amount, type, description, created, userId)

private const val SOME_UPDATES = "some updates"

fun buildUpdateExpenses(
    id: ExpensesId = EXPENSES_ID,
    name: ExpensesName? = null,
    amount: Amount? = null,
    type: ExpensesType? = null,
    description: String? = SOME_UPDATES,
    created: OffsetDateTime? = null,
    userId: UserId = USER_ID
) = UpdateExpensesDto(id, name, amount, type, description, created, userId)