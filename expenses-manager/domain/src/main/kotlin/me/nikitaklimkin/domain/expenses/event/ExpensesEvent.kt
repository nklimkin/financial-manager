package me.nikitaklimkin.domain.expenses.event

import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.model.DomainEvent

data class UpdateExpensesEvent(
    val expensesId: ExpensesId
) : DomainEvent()

data class SaveExpensesEvent(
    val expensesId: ExpensesId
) : DomainEvent()