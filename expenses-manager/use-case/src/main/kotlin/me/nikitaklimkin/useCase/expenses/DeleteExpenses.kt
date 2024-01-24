package me.nikitaklimkin.useCase.expenses

import arrow.core.Either
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.model.DomainError

interface DeleteExpenses {

    fun execute(id: ExpensesId): Either<DeleteExpensesError, Unit>
}

object DeleteExpensesError : DomainError()

