package me.nikitaklimkin.domain.transaction.dto

import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.Category
import me.nikitaklimkin.domain.transaction.Direction
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.domain.transaction.TransactionName
import java.time.OffsetDateTime

data class TransactionDTO(
    val id: TransactionId,
    val accountId: AccountId,
    val name: TransactionName,
    val amount: MoneyAmount,
    val type: Category,
    val direction: Direction,
    val description: String?,
    val created: OffsetDateTime
)

data class UpdateTransaction(
    val name: TransactionName?,
    val amount: MoneyAmount?,
    val type: Category?,
    val direction: Direction?,
    val description: String?,
    val created: OffsetDateTime?
)