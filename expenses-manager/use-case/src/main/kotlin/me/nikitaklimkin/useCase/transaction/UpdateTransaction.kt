package me.nikitaklimkin.useCase.transaction

import arrow.core.Either
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.transaction.Category
import me.nikitaklimkin.domain.transaction.Direction
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.domain.transaction.TransactionName
import me.nikitaklimkin.domain.user.UserId
import java.time.OffsetDateTime

interface UpdateTransaction {

    fun execute(request: UpdateTransactionDTO): Either<UpdateTransactionError, Unit>
}

sealed class UpdateTransactionError {

    data object TransactionNotFound : UpdateTransactionError()

}

data class UpdateTransactionDTO(
    val id: TransactionId,
    val userId: UserId,
    val name: TransactionName?,
    val amount: MoneyAmount?,
    val type: Category?,
    val direction: Direction?,
    val description: String?,
    val created: OffsetDateTime?
)

fun UpdateTransactionDTO.toDomainDto(): me.nikitaklimkin.domain.transaction.dto.UpdateTransaction {
    return me.nikitaklimkin.domain.transaction.dto.UpdateTransaction(
        name,
        amount,
        type,
        direction,
        description,
        created
    )
}