package me.nikitaklimkin.domain

import me.nikitaklimkin.domain.account.*
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

val ACCOUNT_ID = AccountId(UUID.randomUUID())
val ACCOUNT_ID_2 = AccountId(UUID.randomUUID())
val ACCOUNT_ID_3 = AccountId(UUID.randomUUID())
val ACCOUNT_ID_4 = AccountId(UUID.randomUUID())
val TEST_BANK_NAME = BankName.from("Test-Bank").getOrNull()!!
val TEST_BANK_NAME_2 = BankName.from("Test-Bank-2").getOrNull()!!
val TEST_ACCOUNT_DESCRIPTION = AccountDescription.from("Test-Account-Description").getOrNull()!!
val TEST_ACCOUNT_DESCRIPTION_2 = AccountDescription.from("Test-Account-Description-2").getOrNull()!!
val TEST_MONEY_AMOUNT = MoneyAmount.from(BigDecimal.TEN)
val TEST_MONEY_AMOUNT_2 = MoneyAmount.from(BigDecimal("21.1"))
val TEST_INTEREST = Interest.from(5.1).getOrNull()!!
val TEST_INTEREST_2 = Interest.from(1.1).getOrNull()!!

fun buildAccountId() = AccountId.from(UUID.randomUUID().toString()).getOrNull()!!

fun newBrokerAccount() = NewBrokerAccount(USER_ID, TEST_BANK_NAME, TEST_ACCOUNT_DESCRIPTION, TEST_MONEY_AMOUNT)

fun newCardAccount() = NewCardAccount(USER_ID, TEST_BANK_NAME, TEST_ACCOUNT_DESCRIPTION, TEST_MONEY_AMOUNT)

fun newDepositAccount() =
    NewDepositAccount(
        USER_ID,
        TEST_BANK_NAME,
        TEST_ACCOUNT_DESCRIPTION,
        TEST_MONEY_AMOUNT,
        TEST_MONEY_AMOUNT_2,
        TEST_INTEREST,
        OffsetDateTime.MIN,
        OffsetDateTime.MAX
    )

fun newPiggyAccount() =
    NewPiggyAccount(USER_ID, TEST_BANK_NAME, TEST_ACCOUNT_DESCRIPTION, TEST_MONEY_AMOUNT, TEST_INTEREST)

fun brokerAccount(
    id: AccountId = ACCOUNT_ID
) =
    BrokerageAccount(id, USER_ID, TEST_MONEY_AMOUNT, TEST_BANK_NAME, TEST_ACCOUNT_DESCRIPTION, true)

fun cardAccount(id: AccountId = ACCOUNT_ID) =
    CardAccount(id, USER_ID, TEST_MONEY_AMOUNT, TEST_BANK_NAME, TEST_ACCOUNT_DESCRIPTION, true)

fun depositAccount(
    id: AccountId = ACCOUNT_ID,
    openedDateTime: OffsetDateTime = OffsetDateTime.MIN,
    closedDate: OffsetDateTime = OffsetDateTime.MAX
) =
    DepositAccount(
        id,
        USER_ID,
        TEST_MONEY_AMOUNT,
        TEST_MONEY_AMOUNT_2,
        openedDateTime,
        closedDate,
        TEST_INTEREST,
        TEST_BANK_NAME,
        TEST_ACCOUNT_DESCRIPTION,
        true
    )

fun piggyAccount(
    id: AccountId = ACCOUNT_ID
) =
    PiggyBankAccount(
        id,
        USER_ID,
        TEST_MONEY_AMOUNT,
        TEST_BANK_NAME,
        TEST_ACCOUNT_DESCRIPTION,
        TEST_INTEREST,
        true
    )

fun updateBrokerAccount(
    bankName: BankName? = TEST_BANK_NAME,
    description: AccountDescription? = TEST_ACCOUNT_DESCRIPTION
) = UpdateBrokerAccount(ACCOUNT_ID, bankName, description)

fun updateCardAccount(
    bankName: BankName? = TEST_BANK_NAME,
    description: AccountDescription? = TEST_ACCOUNT_DESCRIPTION
) = UpdateCardAccount(ACCOUNT_ID, bankName, description)

fun updateDepositAccount(
    bankName: BankName? = TEST_BANK_NAME,
    description: AccountDescription? = TEST_ACCOUNT_DESCRIPTION,
    interest: Interest? = TEST_INTEREST,
    openDate: OffsetDateTime? = OffsetDateTime.MIN,
    closedDate: OffsetDateTime? = OffsetDateTime.MAX
) = UpdateDepositAccount(ACCOUNT_ID, bankName, description, interest, openDate, closedDate)

fun updatePiggyAccount(
    bankName: BankName? = TEST_BANK_NAME,
    description: AccountDescription? = TEST_ACCOUNT_DESCRIPTION,
    interest: Interest? = TEST_INTEREST
) = UpdatePiggyAccount(ACCOUNT_ID, bankName, description, interest)

