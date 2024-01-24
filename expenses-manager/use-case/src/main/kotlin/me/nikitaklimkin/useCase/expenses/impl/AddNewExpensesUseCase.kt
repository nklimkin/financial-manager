package me.nikitaklimkin.useCase.expenses.impl

import arrow.core.Either
import arrow.core.flatMap
import me.nikitaklimkin.domain.expenses.ExpensesIdGenerator
import me.nikitaklimkin.domain.expenses.dto.SaveExpensesDto
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.useCase.expenses.AddNewExpenses
import me.nikitaklimkin.useCase.expenses.AddNewExpensesDto
import me.nikitaklimkin.useCase.expenses.access.ExpensesExtractor
import me.nikitaklimkin.useCase.expenses.access.ExpensesPersistence
import me.nikitaklimkin.useCase.user.impl.AddNewUserUseCase
import mu.KotlinLogging
import java.time.OffsetDateTime

private val log = KotlinLogging.logger { }

class AddNewExpensesUseCase(
    private val expensesExtractor: ExpensesExtractor,
    private val expensesPersistence: ExpensesPersistence,
    private val expensesIdGenerator: ExpensesIdGenerator
) : AddNewExpenses {
    override fun execute(request: AddNewExpensesDto): Either<DomainError, Unit> {
        log.debug { "Execute add new expenses use case for userId = [${request.userId}]" }
        log.trace { "Execute add new expenses use case = [$request]" }
        return expensesExtractor.findByUserId(request.userId)
            .flatMap { expenses ->
                expenses.addExpenses(
                    expensesIdGenerator,
                    request.toDomainDto()
                )
                expensesPersistence.save(expenses)
            }
    }

}

fun AddNewExpensesDto.toDomainDto(): SaveExpensesDto {
    return SaveExpensesDto(
        name,
        amount,
        type,
        description,
        created = created ?: OffsetDateTime.now()
    )
}