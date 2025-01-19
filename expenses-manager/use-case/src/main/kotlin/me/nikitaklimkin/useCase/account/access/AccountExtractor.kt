package me.nikitaklimkin.useCase.account.access

import me.nikitaklimkin.domain.account.Account
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.user.User

interface AccountExtractor {

    fun findByUser(user: User): Collection<Account>?

    fun findById(accountId: AccountId): Account?

    fun isAccountExists(accountId: AccountId): Boolean
}