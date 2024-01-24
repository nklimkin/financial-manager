package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.Either
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.useCase.expenses.DeleteExpenses
import me.nikitaklimkin.useCase.expenses.DeleteExpensesError
import me.nikitaklimkin.useCase.expenses.access.ExpensesPersistence
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class DeleteExpensesUseCase(private val expensesPersistence: ExpensesPersistence) : DeleteExpenses {
    override fun execute(id: ExpensesId): Either<DeleteExpensesError, Unit> {
        log.debug { "Execute delete expenses for id = [$id]" }
        return expensesPersistence.delete(id)
            .mapLeft { DeleteExpensesError }
    }
}