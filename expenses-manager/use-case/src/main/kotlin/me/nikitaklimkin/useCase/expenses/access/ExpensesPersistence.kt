package me.nikitaklimkin.useCase.expenses.access

import arrow.core.Either
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.UserExpenses
import me.nikitaklimkin.model.DomainError

interface ExpensesPersistence {

    fun save(userExpenses: UserExpenses): Either<DomainError, Unit>

    fun update(userExpenses: UserExpenses): Either<DomainError, Unit>

    fun delete(expensesId: ExpensesId): Either<DomainError, Unit>
}