package me.nikitaklimkin.domain.transaction.event

import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.model.DomainEvent

data class UpdateTransactionEvent(
    val transactionId: TransactionId
) : DomainEvent()

data class SaveTransactionEvent(
    val transactionId: TransactionId
) : DomainEvent()