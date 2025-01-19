package me.nikitaklimkin.useCase.transaction.access

import me.nikitaklimkin.domain.account.Account
import me.nikitaklimkin.domain.transaction.Transaction
import me.nikitaklimkin.domain.transaction.TransactionId

interface TransactionExtractor {

    fun findById(transactionId: TransactionId): Transaction?

    fun findByAccount(account: Account): Collection<Transaction>

}