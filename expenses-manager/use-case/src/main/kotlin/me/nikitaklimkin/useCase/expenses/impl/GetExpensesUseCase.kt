package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.Either
import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.useCase.expenses.ExpensesDetails
import me.nikitaklimkin.useCase.expenses.ExpensesInfo
import me.nikitaklimkin.useCase.expenses.GetExpenses
import me.nikitaklimkin.useCase.expenses.GetExpensesError
import me.nikitaklimkin.useCase.expenses.access.ExpensesExtractor
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class GetExpensesUseCase(private val expensesExtractor: ExpensesExtractor) : GetExpenses {
    override fun executeByUser(userId: UserId): Either<GetExpensesError, ExpensesInfo> {
        log.debug { "Execute get expenses use case for userId = [$userId]" }
        return expensesExtractor.findByUserId(userId)
            .map { it.getExpenses() }
            .map { expenses -> ExpensesInfo(userId, expenses.map { it.toDetails() }) }
            .mapLeft { GetExpensesError }
    }
}

fun ExpensesDto.toDetails(): ExpensesDetails {
    return ExpensesDetails(
        id,
        name,
        amount,
        type,
        description,
        created
    )
}