package me.nikitaklimkin.useCase.expenses.access

import arrow.core.Either
import me.nikitaklimkin.domain.expenses.UserExpenses
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainError

interface ExpensesExtractor {

    fun findByUserId(userId: UserId): Either<ExpensesNotFoundError, UserExpenses>

}

object ExpensesNotFoundError : DomainError()