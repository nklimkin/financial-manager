package me.nikitaklimkin.useCase.account

import arrow.core.Either
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.account.RemoveAccount
import me.nikitaklimkin.domain.user.UserId

interface RemoveAccount {

    fun execute(request: RemoveAccountRequestDto): Either<RemoveAccountError, Unit>

}

data class RemoveAccountRequestDto(
    val userId: UserId,
    val accountId: AccountId
) {

    fun toDomainDto() = RemoveAccount(accountId)

}

sealed class RemoveAccountError() {

    data object UserNotFound : RemoveAccountError()

    data object AccountNotFound : RemoveAccountError()

}