package me.nikitaklimkin.useCase.expenses

import arrow.core.Either
import kotlinx.serialization.Serializable
import me.nikitaklimkin.domain.expenses.Amount
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.ExpensesName
import me.nikitaklimkin.domain.expenses.ExpensesType
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainError
import java.time.OffsetDateTime

interface UpdateExpenses {

    fun execute(request: UpdateExpensesDto): Either<UpdateExpensesError, Unit>
}

object UpdateExpensesError : DomainError()

data class UpdateExpensesDto(
    val id: ExpensesId,
    val name: ExpensesName?,
    val amount: Amount?,
    val type: ExpensesType?,
    val description: String?,
    val created: OffsetDateTime?,
    val userId: UserId
)