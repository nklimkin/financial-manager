package me.nikitaklimkin.domain.account

interface AccountIdGenerator {

    fun generate(): AccountId
}