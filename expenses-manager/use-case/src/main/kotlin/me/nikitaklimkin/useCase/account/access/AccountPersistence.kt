package me.nikitaklimkin.useCase.account.access

import me.nikitaklimkin.domain.account.Account

interface AccountPersistence {

    fun save(userAccounts: Account)
}