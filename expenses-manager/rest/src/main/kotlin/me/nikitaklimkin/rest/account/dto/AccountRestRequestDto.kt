package me.nikitaklimkin.rest.account.dto

import java.time.OffsetDateTime

class AddNewBrokerAccountRequest(
    userId: String,
    bankName: String,
    description: String,
    val initBalance: Double
) : AddNewAccountRequest(userId, bankName, description)

class AddNewCardAccountRequest(
    userId: String,
    bankName: String,
    description: String,
    val initBalance: Double
) : AddNewAccountRequest(userId, bankName, description)

class AddNewDepositAccountRequest(
    userId: String,
    bankName: String,
    description: String,
    val initialBalance: Double,
    val expectedFinalBalance: Double,
    val openedDate: OffsetDateTime,
    val closedDate: OffsetDateTime,
    val interest: Double,
) : AddNewAccountRequest(userId, bankName, description)

class AddNewPiggyAccountRequest(
    userId: String,
    bankName: String,
    description: String,
    val initialBalance: Double,
    val interest: Double
) : AddNewAccountRequest(userId, bankName, description)

sealed class AddNewAccountRequest(
    val userId: String,
    val bankName: String,
    val description: String
)

class UpdateBrokerAccountRequest(
    userId: String,
    accountId: String,
    bankName: String?,
    description: String?,
) : UpdateAccountRequest(userId, accountId, bankName, description)

class UpdateCardAccountRequest(
    userId: String,
    accountId: String,
    bankName: String?,
    description: String?
) : UpdateAccountRequest(userId, accountId, bankName, description)

class UpdateDepositAccountRequest(
    userId: String,
    accountId: String,
    bankName: String?,
    description: String?,
    val interest: Double?,
    val openedDate: OffsetDateTime?,
    val closedDate: OffsetDateTime?
) : UpdateAccountRequest(userId, accountId, bankName, description)

class UpdatePiggyAccountRequest(
    userId: String,
    accountId: String,
    bankName: String?,
    description: String?,
    val interest: Double?
) : UpdateAccountRequest(userId, accountId, bankName, description)

sealed class UpdateAccountRequest(
    val userId: String,
    val accountId: String,
    val bankName: String?,
    val description: String?
)