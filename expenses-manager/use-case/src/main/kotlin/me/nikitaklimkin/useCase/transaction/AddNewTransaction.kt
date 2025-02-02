package me.nikitaklimkin.useCase.transaction

import arrow.core.Either
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.Category
import me.nikitaklimkin.domain.transaction.Direction
import me.nikitaklimkin.domain.transaction.TransactionName
import me.nikitaklimkin.domain.user.UserId

interface AddNewTransaction {

    fun execute(request: AddNewTransactionDTO): Either<AddNewTransactionError, Unit>

}

data class AddNewTransactionDTO(
    val accountId: AccountId,
    val userId: UserId,
    val name: TransactionName,
    val amount: MoneyAmount,
    val type: Category,
    val direction: Direction,
    val description: String?
)

sealed class AddNewTransactionError {

    data object AccountNotFound : AddNewTransactionError()

    data object TransactionAlreadyExists : AddNewTransactionError()

}