package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.Either
import arrow.core.flatMap
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.useCase.expenses.UpdateExpenses
import me.nikitaklimkin.useCase.expenses.UpdateExpensesDto
import me.nikitaklimkin.useCase.expenses.UpdateExpensesError
import me.nikitaklimkin.useCase.expenses.access.ExpensesExtractor
import me.nikitaklimkin.useCase.expenses.access.ExpensesPersistence
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class UpdateExpensesUseCase(
    private val expensesExtractor: ExpensesExtractor,
    private val expensesPersistence: ExpensesPersistence
) : UpdateExpenses {
    override fun execute(request: UpdateExpensesDto): Either<UpdateExpensesError, Unit> {
        log.debug {
            "Execute update expenses use case by userId = [${request.userId}] and expensesId = [${request.id}]"
        }
        log.trace { "Execute update expenses use case = [$request]" }
        return expensesExtractor.findByUserId(request.userId)
            .flatMap { expenses ->
                expenses.updateExpenses(request.toDomainDto())
                    .onRight { _ -> expensesPersistence.update(expenses) }
            }
            .mapLeft { UpdateExpensesError }
            .map { }
    }
}

fun UpdateExpensesDto.toDomainDto(): me.nikitaklimkin.domain.expenses.dto.UpdateExpensesDto {
    return me.nikitaklimkin.domain.expenses.dto.UpdateExpensesDto(
        id,
        name,
        amount,
        type,
        description,
        created
    )
}