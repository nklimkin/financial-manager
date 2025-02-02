package me.nikitaklimkin.rest.transaction.dto

import kotlinx.serialization.Serializable
import me.nikitaklimkin.domain.transaction.dto.TransactionDTO
import me.nikitaklimkin.rest.serializer.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class AddTransactionRestRequest(
    val accountId: String,
    val name: String,
    val amount: Double,
    val type: String,
    val direction: String,
    val description: String?,
    @Serializable(OffsetDateTimeSerializer::class) val created: OffsetDateTime?
)

@Serializable
data class UpdateTransactionRestRequest(
    val id: String,
    val name: String?,
    val amount: Double?,
    val type: String?,
    val direction: String?,
    val description: String?,
    @Serializable(OffsetDateTimeSerializer::class) val created: OffsetDateTime?
)

@Serializable
data class TransactionRestResponse(
    val id: String,
    val name: String,
    val amount: Double,
    val type: String,
    val direction: String,
    val description: String?,
    @Serializable(OffsetDateTimeSerializer::class)
    val created: OffsetDateTime
)

fun TransactionDTO.toDetails(): TransactionRestResponse {
    return TransactionRestResponse(
        id.toString(),
        name.toStringValue(),
        amount.value.toDouble(),
        type.toStringValue(),
        direction.toString(),
        description,
        created
    )
}