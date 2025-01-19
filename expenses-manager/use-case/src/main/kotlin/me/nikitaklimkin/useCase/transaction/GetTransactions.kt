package me.nikitaklimkin.useCase.transaction

import arrow.core.Either
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.dto.TransactionDTO
import me.nikitaklimkin.domain.user.UserId

interface GetTransactions {

    fun execute(request: GetTransactionsDTO): Either<GetTransactionsError, Collection<TransactionDTO>>
}

data class GetTransactionsDTO(val userId: UserId, val accountId: AccountId)

sealed class GetTransactionsError {

    data object TransactionsNotFound : GetTransactionsError()

}