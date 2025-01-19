package me.nikitaklimkin.persistence

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.persistence.account.model.AccountPersistenceModel
import me.nikitaklimkin.persistence.account.model.AccountPersistenceType
import me.nikitaklimkin.persistence.account.model.toPersistenceId
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

val TEST_PERSISTENCE_OPENED_DATE = OffsetDateTime.of(
    LocalDateTime.of(2024, 10, 10, 10, 10, 10),
    ZoneOffset.UTC
)

val TEST_PERSISTENCE_CLOSED_DATE = OffsetDateTime.of(
    LocalDateTime.of(2025, 10, 10, 10, 10, 10),
    ZoneOffset.UTC
)

fun buildDepositAccountPersistenceModel(
    id: AccountId = ACCOUNT_ID,
    userId: UserId = USER_ID
) = AccountPersistenceModel(
    id.toPersistenceId(),
    userId.toPersistenceId(),
    AccountPersistenceType.DEPOSIT,
    TEST_BANK_NAME.value,
    TEST_ACCOUNT_DESCRIPTION.value,
    TEST_MONEY_AMOUNT.value,
    TEST_MONEY_AMOUNT_2.value,
    TEST_INTEREST.value,
    TEST_PERSISTENCE_OPENED_DATE,
    TEST_PERSISTENCE_CLOSED_DATE,
    true
)

fun buildCardAccountPersistenceModel(
    id: AccountId = ACCOUNT_ID,
    userId: UserId = USER_ID
) = AccountPersistenceModel(
    id.toPersistenceId(),
    userId.toPersistenceId(),
    AccountPersistenceType.CARD,
    TEST_BANK_NAME.value,
    TEST_ACCOUNT_DESCRIPTION.value,
    TEST_MONEY_AMOUNT.value,
    null,
    null,
    null,
    null,
    true
)

fun buildPiggyAccountPersistenceModel(
    id: AccountId = ACCOUNT_ID,
    userId: UserId = USER_ID
) = AccountPersistenceModel(
    id.toPersistenceId(),
    userId.toPersistenceId(),
    AccountPersistenceType.PIGGY,
    TEST_BANK_NAME.value,
    TEST_ACCOUNT_DESCRIPTION.value,
    TEST_MONEY_AMOUNT.value,
    null,
    TEST_INTEREST.value,
    null,
    null,
    true
)

fun buildBrokerageAccountPersistenceModel(
    id: AccountId = ACCOUNT_ID,
    userId: UserId = USER_ID
) = AccountPersistenceModel(
    id.toPersistenceId(),
    userId.toPersistenceId(),
    AccountPersistenceType.BROKERAGE,
    TEST_BANK_NAME.value,
    TEST_ACCOUNT_DESCRIPTION.value,
    TEST_MONEY_AMOUNT.value,
    null,
    null,
    null,
    null,
    true
)