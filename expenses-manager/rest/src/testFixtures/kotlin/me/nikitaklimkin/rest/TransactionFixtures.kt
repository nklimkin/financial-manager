package me.nikitaklimkin.rest

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.domain.transaction.TransactionName
import me.nikitaklimkin.domain.transaction.Category
import me.nikitaklimkin.rest.transaction.dto.AddTransactionRestRequest
import me.nikitaklimkin.rest.transaction.dto.TransactionRestResponse
import me.nikitaklimkin.rest.transaction.dto.UpdateTransactionRestRequest
import java.time.OffsetDateTime
import java.util.*


const val INVALID_ID = "1234"
const val INVALID_DIRECTION = "INVALID_DIRECTION"
const val VALID_DIRECTION = "IN"

fun buildValidAddTransactionRestRequest(
    accountId: String = VALID_ACCOUNT_ID,
    name: String = VALID_NAME,
    amount: Double = VALID_AMOUNT.toDouble(),
    type: String = VALID_TYPE,
    direction: String = VALID_DIRECTION,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now()
) = AddTransactionRestRequest(
    accountId,
    name,
    amount,
    type,
    direction,
    description,
    created
)

fun buildInvalidAddTransactionRestRequest(
    accountId: String = VALID_ACCOUNT_ID,
    name: String = INVALID_NAME,
    amount: Double = INVALID_AMOUNT,
    type: String = INVALID_TYPE,
    direction: String = INVALID_DIRECTION,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now()
) = AddTransactionRestRequest(
    accountId,
    name,
    amount,
    type,
    direction,
    description,
    created
)

fun buildValidUpdateTransactionRestRequest(
    id: String = UUID.randomUUID().toString(),
    name: String = VALID_NAME,
    amount: Double = VALID_AMOUNT.toDouble(),
    type: String = VALID_TYPE,
    direction: String = VALID_DIRECTION,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now()
) = UpdateTransactionRestRequest(
    id,
    name,
    amount,
    type,
    direction,
    description,
    created
)

fun buildInvalidUpdateTransactionRestRequest(
    id: String = INVALID_ID,
    name: String = INVALID_NAME,
    amount: Double = INVALID_AMOUNT,
    type: String = INVALID_TYPE,
    direction: String = INVALID_DIRECTION,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now()
) = UpdateTransactionRestRequest(
    id,
    name,
    amount,
    type,
    direction,
    description,
    created
)

fun buildTransactionRestResponse(
    id: TransactionId = TRANSACTION_ID,
    name: TransactionName = buildName(),
    amount: MoneyAmount = buildAmount(),
    type: Category = buildType(),
    direction: String = VALID_DIRECTION,
    description: String? = DESCRIPTION,
    created: OffsetDateTime = OffsetDateTime.now()
) = TransactionRestResponse(
    id.toString(),
    name.toStringValue(),
    amount.value.toDouble(),
    type.toStringValue(),
    direction,
    description,
    created
)

