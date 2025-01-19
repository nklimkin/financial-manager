package me.nikitaklimkin.useCase.transaction

import arrow.core.Either
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.domain.user.UserId

interface RemoveTransaction {

    fun execute(request: DeleteTransactionDTO): Either<RemoveTransactionError, Unit>
}

data class DeleteTransactionDTO(
    val userId: UserId,
    val transactionId: TransactionId
)

sealed class RemoveTransactionError {

    data object TransactionNotFound : RemoveTransactionError()

}

