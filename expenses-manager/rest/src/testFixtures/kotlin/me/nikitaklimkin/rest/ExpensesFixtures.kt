package me.nikitaklimkin.rest

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.rest.expenses.dto.AddExpensesRestRequest
import me.nikitaklimkin.rest.expenses.dto.UpdateExpensesRestRequest
import java.time.OffsetDateTime
import java.util.*


private const val INVALID_ID = "1234"

fun buildValidAddExpensesRestRequest(
    name: String = VALID_NAME,
    amount: Double = VALID_AMOUNT,
    type: String = VALID_TYPE,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now(),
    userId: String = UUID.randomUUID().toString()
) = AddExpensesRestRequest(
    name,
    amount,
    type,
    description,
    created,
    userId
)

fun buildInvalidAddExpensesRestRequest(
    name: String = INVALID_NAME,
    amount: Double = INVALID_AMOUNT,
    type: String = INVALID_TYPE,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now(),
    userId: String = UUID.randomUUID().toString()
) = AddExpensesRestRequest(
    name,
    amount,
    type,
    description,
    created,
    userId
)

fun buildValidUpdateExpensesRestRequest(
    id: String = UUID.randomUUID().toString(),
    name: String = VALID_NAME,
    amount: Double = VALID_AMOUNT,
    type: String = VALID_TYPE,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now(),
    userId: String = UUID.randomUUID().toString()
) = UpdateExpensesRestRequest(
    id,
    name,
    amount,
    type,
    description,
    created,
    userId
)

fun buildInvalidUpdateExpensesRestRequest(
    id: String = INVALID_ID,
    name: String = INVALID_NAME,
    amount: Double = INVALID_AMOUNT,
    type: String = INVALID_TYPE,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now(),
    userId: String = UUID.randomUUID().toString()
) = UpdateExpensesRestRequest(
    id,
    name,
    amount,
    type,
    description,
    created,
    userId
)

