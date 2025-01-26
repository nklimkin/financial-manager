package me.nikitaklimkin.rest.account.dto

import kotlinx.serialization.Serializable
import me.nikitaklimkin.rest.serializer.OffsetDateTimeSerializer
import me.nikitaklimkin.useCase.account.AccountDto
import java.time.OffsetDateTime

@Serializable
class AddNewBrokerAccountRestRequest(
    override val userId: String,
    override val bankName: String,
    override val description: String,
    val initBalance: Double
) : AddNewAccountRequest()

@Serializable
class AddNewCardAccountRestRequest(
    override val userId: String,
    override val bankName: String,
    override val description: String,
    val initBalance: Double
) : AddNewAccountRequest()

@Serializable
class AddNewDepositAccountRestRequest(
    override val userId: String,
    override val bankName: String,
    override val description: String,
    val initialBalance: Double,
    val expectedFinalBalance: Double,
    @Serializable(OffsetDateTimeSerializer::class) val openedDate: OffsetDateTime,
    @Serializable(OffsetDateTimeSerializer::class) val closedDate: OffsetDateTime,
    val interest: Double,
) : AddNewAccountRequest()

@Serializable
class AddNewPiggyAccountRestRequest(
    override val userId: String,
    override val bankName: String,
    override val description: String,
    val initialBalance: Double,
    val interest: Double
) : AddNewAccountRequest()

@Serializable
sealed class AddNewAccountRequest {
    abstract val userId: String
    abstract val bankName: String
    abstract val description: String
}

@Serializable
class UpdateBrokerAccountRestRequest(
    override val userId: String,
    override val accountId: String,
    override val bankName: String?,
    override val description: String?,
) : UpdateAccountRequest()

@Serializable
class UpdateCardAccountRestRequest(
    override val userId: String,
    override val accountId: String,
    override val bankName: String?,
    override val description: String?
) : UpdateAccountRequest()

@Serializable
class UpdateDepositAccountRestRequest(
    override val userId: String,
    override val accountId: String,
    override val bankName: String?,
    override val description: String?,
    val interest: Double?,
    @Serializable(OffsetDateTimeSerializer::class) val openedDate: OffsetDateTime?,
    @Serializable(OffsetDateTimeSerializer::class) val closedDate: OffsetDateTime?
) : UpdateAccountRequest()

@Serializable
class UpdatePiggyAccountRestRequest(
    override val userId: String,
    override val accountId: String,
    override val bankName: String?,
    override val description: String?,
    val interest: Double?
) : UpdateAccountRequest()

@Serializable
sealed class UpdateAccountRequest {
    abstract val userId: String
    abstract val accountId: String
    abstract val bankName: String?
    abstract val description: String?
}

@Serializable
class AccountRestResponseDto(
    val accountId: String,
    val bankName: String,
    val description: String
)

fun AccountDto.toRestResponse(): AccountRestResponseDto {
    return AccountRestResponseDto(
        accountId.toUuid().toString(),
        bankName.value,
        description.value
    )
}