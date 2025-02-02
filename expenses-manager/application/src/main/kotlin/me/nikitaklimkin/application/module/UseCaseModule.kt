package me.nikitaklimkin.application.module

import me.nikitaklimkin.useCase.account.AddAccount
import me.nikitaklimkin.useCase.account.GetAccounts
import me.nikitaklimkin.useCase.account.RemoveAccount
import me.nikitaklimkin.useCase.account.UpdateAccount
import me.nikitaklimkin.useCase.account.impl.AddAccountUseCase
import me.nikitaklimkin.useCase.account.impl.GetAccountsUseCase
import me.nikitaklimkin.useCase.account.impl.RemoveAccountUseCase
import me.nikitaklimkin.useCase.account.impl.UpdateAccountUseCase
import me.nikitaklimkin.useCase.transaction.AddNewTransaction
import me.nikitaklimkin.useCase.transaction.GetTransactions
import me.nikitaklimkin.useCase.transaction.RemoveTransaction
import me.nikitaklimkin.useCase.transaction.UpdateTransaction
import me.nikitaklimkin.useCase.transaction.impl.AddNewTransactionUseCase
import me.nikitaklimkin.useCase.transaction.impl.GetTransactionsUseCase
import me.nikitaklimkin.useCase.transaction.impl.RemoveTransactionUseCase
import me.nikitaklimkin.useCase.transaction.impl.UpdateTransactionUseCase
import me.nikitaklimkin.useCase.user.UserLogin
import me.nikitaklimkin.useCase.user.impl.UserLoginUseCase
import org.koin.dsl.module

val useCasesModule = module(createdAtStart = true) {
    single<UserLogin> { UserLoginUseCase(get(), get(), get()) }
    single<AddNewTransaction> { AddNewTransactionUseCase(get(), get(), get()) }
    single<GetTransactions> { GetTransactionsUseCase(get(), get(), get()) }
    single<RemoveTransaction> { RemoveTransactionUseCase(get(), get(), get()) }
    single<UpdateTransaction> { UpdateTransactionUseCase(get(), get(), get()) }
    single<AddAccount> { AddAccountUseCase(get(), get(), get()) }
    single<GetAccounts> { GetAccountsUseCase(get(), get()) }
    single<RemoveAccount> { RemoveAccountUseCase(get(), get(), get()) }
    single<UpdateAccount> { UpdateAccountUseCase(get(), get(), get()) }
}