package me.nikitaklimkin.rest.expenses.dto

import kotlinx.serialization.Serializable
import me.nikitaklimkin.rest.serializer.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class AddExpensesRestRequest(
    val name: String,
    val amount: Double,
    val type: String,
    val description: String?,
    @Serializable(OffsetDateTimeSerializer::class) val created: OffsetDateTime?,
    val userId: String
)

@Serializable
data class UpdateExpensesRestRequest(
    val id: String,
    val name: String?,
    val amount: Double?,
    val type: String?,
    val description: String?,
    @Serializable(OffsetDateTimeSerializer::class) val created: OffsetDateTime?,
    val userId: String
)