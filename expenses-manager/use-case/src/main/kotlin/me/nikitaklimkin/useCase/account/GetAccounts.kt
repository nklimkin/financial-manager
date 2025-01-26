package me.nikitaklimkin.useCase.account

import arrow.core.Either
import me.nikitaklimkin.domain.account.AccountDescription
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.account.BankName
import me.nikitaklimkin.domain.user.UserId

interface GetAccounts {

    fun execute(request: GetAccountRequest): Either<GetAccountError, GetAccountResponse>
}

data class GetAccountRequest(val userId: UserId)

data class GetAccountResponse(val userId: UserId, val accounts: List<AccountDto>)

data class AccountDto(val accountId: AccountId, val bankName: BankName, val description: AccountDescription)

sealed class GetAccountError {

    data object UserNotFound : GetAccountError()

}