package me.nikitaklimkin.useCase.account.access

import arrow.core.Either
import me.nikitaklimkin.domain.account.Account

interface AccountPersistence {

    fun save(userAccounts: Account): Either<AccountPersistenceError.AccountAlreadyExists, Unit>

    fun update(userAccounts: Account): Either<AccountPersistenceError.AccountNotFound, Unit>
}

sealed class AccountPersistenceError {

    data object AccountAlreadyExists : AccountPersistenceError()
    data object AccountNotFound : AccountPersistenceError()

}