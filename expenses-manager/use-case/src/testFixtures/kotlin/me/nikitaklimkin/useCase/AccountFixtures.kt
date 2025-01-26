package me.nikitaklimkin.useCase

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.useCase.account.*
import java.time.OffsetDateTime


fun buildAddBrokerAccountRequest() = AddNewBrokerAccountDTO(
    USER_ID,
    TEST_BANK_NAME,
    TEST_ACCOUNT_DESCRIPTION,
    TEST_MONEY_AMOUNT
)

fun buildAddCardAccountRequest() = AddNewCardAccountDTO(
    USER_ID,
    TEST_BANK_NAME,
    TEST_ACCOUNT_DESCRIPTION,
    TEST_MONEY_AMOUNT
)

fun buildAddDepositAccountRequest() = AddNewDepositAccountDTO(
    USER_ID,
    TEST_BANK_NAME,
    TEST_ACCOUNT_DESCRIPTION,
    TEST_MONEY_AMOUNT,
    TEST_MONEY_AMOUNT_2,
    OffsetDateTime.MIN,
    OffsetDateTime.MAX,
    TEST_INTEREST
)

fun buildAddPiggyAccountRequest() = AddNewPiggyAccountDTO(
    USER_ID,
    TEST_BANK_NAME,
    TEST_ACCOUNT_DESCRIPTION,
    TEST_MONEY_AMOUNT,
    TEST_INTEREST
)

fun buildUpdateBrokerAccountRequest() = UpdateBrokerAccountRequest(
    USER_ID,
    ACCOUNT_ID,
    TEST_BANK_NAME_2,
    null
)

fun buildUpdateCardAccountRequest() = UpdateCardAccountRequest(
    USER_ID,
    ACCOUNT_ID,
    TEST_BANK_NAME_2,
    null
)

fun buildUpdateDepositAccountRequest() = UpdateDepositAccountRequest(
    USER_ID,
    ACCOUNT_ID,
    TEST_BANK_NAME_2,
    null,
    null,
    null,
    null
)

fun buildUpdatePiggyAccountRequest() = UpdatePiggyAccountRequest(
    USER_ID,
    ACCOUNT_ID,
    TEST_BANK_NAME_2,
    null,
    null
)

