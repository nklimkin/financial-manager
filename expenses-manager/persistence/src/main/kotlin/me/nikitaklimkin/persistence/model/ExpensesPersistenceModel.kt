package me.nikitaklimkin.persistence.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import me.nikitaklimkin.domain.Amount
import me.nikitaklimkin.domain.Expenses
import me.nikitaklimkin.domain.ExpensesId
import me.nikitaklimkin.domain.UserId
import me.nikitaklimkin.model.DomainError
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import java.time.OffsetDateTime
import java.util.*

class ExpensesPersistenceModel(
    @BsonId
    override val id: Id<ExpensesPersistenceModel>,
    val amount: Double,
    val type: String,
    val description: String?,
    val userId: UUID,
    val created: OffsetDateTime
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(expenses: Expenses): ExpensesPersistenceModel {
            return ExpensesPersistenceModel(
                expenses.id.toString().toId(),
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
                        ExpensesId(UUID.fromString(id.toString())),
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