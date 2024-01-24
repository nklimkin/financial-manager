package me.nikitaklimkin.useCase.expenses

import arrow.core.Either
import me.nikitaklimkin.domain.expenses.Amount
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.ExpensesName
import me.nikitaklimkin.domain.expenses.ExpensesType
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainError
import java.time.OffsetDateTime

interface GetExpenses {

    fun executeByUser(userId: UserId): Either<GetExpensesError, ExpensesInfo>
}

object GetExpensesError : DomainError()

data class ExpensesInfo(
    val userId: UserId,
    val details: Collection<ExpensesDetails>
)

data class ExpensesDetails(
    val id: ExpensesId,
    val name: ExpensesName,
    val amount: Amount,
    val type: ExpensesType,
    val description: String?,
    val created: OffsetDateTime
)