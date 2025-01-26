package me.nikitaklimkin.rest

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.rest.account.dto.*
import me.nikitaklimkin.useCase.account.AccountDto
import me.nikitaklimkin.useCase.account.GetAccountResponse
import java.time.OffsetDateTime
import java.util.*

val VALID_USER_ID = UUID.randomUUID().toString()
const val VALID_BANK_NAME = "test-bank-name"
const val VALID_DESCRIPTION = "test-description"
const val TEST_INITIAL_BALANCE = 40.0
const val TEST_EXPECTED_BALANCE = 50.0
const val TEST_INTEREST = 4.1

const val INVALID_USER_ID = ""
const val INVALID_BANK_NAME = ""
const val INVALID_DESCRIPTION = ""

fun buildAddDepositAccountRestRequest() = AddNewDepositAccountRestRequest(
    VALID_USER_ID,
    VALID_BANK_NAME,
    VALID_DESCRIPTION,
    TEST_INITIAL_BALANCE,
    TEST_EXPECTED_BALANCE,
    OffsetDateTime.now(),
    OffsetDateTime.now(),
    TEST_INTEREST
)

fun buildInvalidAddDepositAccountRestRequest() = AddNewDepositAccountRestRequest(
    INVALID_USER_ID,
    INVALID_BANK_NAME,
    INVALID_DESCRIPTION,
    TEST_INITIAL_BALANCE,
    TEST_EXPECTED_BALANCE,
    OffsetDateTime.now(),
    OffsetDateTime.now(),
    TEST_INTEREST
)

fun buildAddCardAccountRestRequest() = AddNewCardAccountRestRequest(
    VALID_USER_ID,
    VALID_BANK_NAME,
    VALID_DESCRIPTION,
    TEST_INITIAL_BALANCE
)

fun buildInvalidAddCardAccountRestRequest() = AddNewCardAccountRestRequest(
    INVALID_USER_ID,
    INVALID_BANK_NAME,
    INVALID_DESCRIPTION,
    TEST_INITIAL_BALANCE
)

fun buildAddBrokerageAccountRestRequest() = AddNewBrokerAccountRestRequest(
    VALID_USER_ID,
    VALID_BANK_NAME,
    VALID_DESCRIPTION,
    TEST_INITIAL_BALANCE
)

fun buildInvalidAddBrokerageAccountRestRequest() = AddNewBrokerAccountRestRequest(
    INVALID_USER_ID,
    INVALID_BANK_NAME,
    INVALID_DESCRIPTION,
    TEST_INITIAL_BALANCE
)

fun buildAddPiggyAccountRequest() = AddNewPiggyAccountRestRequest(
    VALID_USER_ID,
    VALID_BANK_NAME,
    VALID_DESCRIPTION,
    TEST_INITIAL_BALANCE,
    TEST_INTEREST
)

fun buildInvalidAddPiggyAccountRequest() = AddNewPiggyAccountRestRequest(
    INVALID_USER_ID,
    INVALID_BANK_NAME,
    INVALID_DESCRIPTION,
    TEST_INITIAL_BALANCE,
    TEST_INTEREST
)

fun buildValidUpdateBrokerAccountRequest() = UpdateBrokerAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    VALID_BANK_NAME,
    null
)

fun buildInvalidUpdateBrokerAccountRequest() = UpdateBrokerAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    INVALID_BANK_NAME,
    null
)

fun buildValidUpdateCardAccountRequest() = UpdateCardAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    VALID_BANK_NAME,
    null
)

fun buildInvalidUpdateCardAccountRequest() = UpdateCardAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    INVALID_BANK_NAME,
    null
)

fun buildValidUpdateDepositAccountRequest() = UpdateDepositAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    VALID_BANK_NAME,
    null,
    null,
    null,
    null
)

fun buildInvalidUpdateDepositAccountRequest() = UpdateDepositAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    INVALID_BANK_NAME,
    null,
    null,
    null,
    null
)

fun buildValidUpdatePiggyAccountRequest() = UpdatePiggyAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    VALID_BANK_NAME,
    null,
    null
)

fun buildInvalidUpdatePiggyAccountRequest() = UpdatePiggyAccountRestRequest(
    VALID_USER_ID,
    VALID_ACCOUNT_ID,
    INVALID_BANK_NAME,
    null,
    null
)

fun buildGetAccountResponse() = GetAccountResponse(USER_ID, listOf(buildAccountDto()))

fun buildAccountDto() = AccountDto(ACCOUNT_ID, TEST_BANK_NAME, TEST_ACCOUNT_DESCRIPTION)

