package me.nikitaklimkin.useCase.expenses

import arrow.core.Either
import me.nikitaklimkin.domain.expenses.Amount
import me.nikitaklimkin.domain.expenses.ExpensesName
import me.nikitaklimkin.domain.expenses.ExpensesType
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainError
import java.time.OffsetDateTime

interface AddNewExpenses {

    fun execute(request: AddNewExpensesDto): Either<DomainError, Unit>

}

data class AddNewExpensesDto(
    val name: ExpensesName,
    val amount: Amount,
    val type: ExpensesType,
    val description: String?,
    val created: OffsetDateTime?,
    val userId: UserId
)