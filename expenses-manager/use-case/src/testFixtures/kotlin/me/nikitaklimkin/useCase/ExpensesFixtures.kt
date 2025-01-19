package me.nikitaklimkin.useCase

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.Category
import me.nikitaklimkin.domain.transaction.Direction
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.domain.transaction.TransactionName
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.useCase.transaction.AddNewTransactionDTO
import me.nikitaklimkin.useCase.transaction.UpdateTransactionDTO
import java.time.OffsetDateTime

fun buildAddNewTransactionDTO(
    accountId: AccountId = ACCOUNT_ID,
    name: TransactionName = buildName(),
    amount: MoneyAmount = buildAmount(),
    type: Category = buildType(),
    direction: Direction = Direction.IN,
    description: String? = DESCRIPTION
) = AddNewTransactionDTO(accountId, name, amount, type, direction, description)

private const val SOME_UPDATES = "some updates"

fun buildUpdateTransactionDTO(
    id: TransactionId = TRANSACTION_ID,
    name: TransactionName? = null,
    amount: MoneyAmount? = null,
    type: Category? = null,
    direction: Direction = Direction.IN,
    description: String? = SOME_UPDATES,
    created: OffsetDateTime? = null,
    userId: UserId = USER_ID
) = UpdateTransactionDTO(id, userId, name, amount, type, direction, description, created)