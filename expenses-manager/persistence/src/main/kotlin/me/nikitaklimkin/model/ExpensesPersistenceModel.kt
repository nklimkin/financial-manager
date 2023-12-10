package me.nikitaklimkin.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.*
import org.bson.codecs.pojo.annotations.BsonId
import java.time.OffsetDateTime
import java.util.UUID

class ExpensesPersistenceModel(
    @BsonId
    override val id: UUID,
    val amount: Double,
    val type: String,
    val description: String?,
    val userId: UUID,
    val created: OffsetDateTime
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(expenses: Expenses): ExpensesPersistenceModel {
            return ExpensesPersistenceModel(
                expenses.id.toUuid(),
                expenses.amount.toDoubleValue(),
                expenses.type,
                expenses.description,
                expenses.userId.toUuid(),
                expenses.created
            )
        }

    }

    fun toBusiness(): Either<DomainError, Expenses> {
        val amountValue = Amount.from(amount)
        return if (amountValue.isLeft()) {
            return amountValue.leftOrNull()!!.left()
        } else {
            amountValue
                .flatMap { currentAmount ->
                    Expenses.build(
                        ExpensesId(id),
                        currentAmount,
                        type,
                        description,
                        UserId(userId),
                        created
                    )
                }
        }
    }
}