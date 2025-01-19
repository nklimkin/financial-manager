package me.nikitaklimkin.domain.transaction

interface TransactionIdGenerator {

    fun generate(): TransactionId
}