package me.nikitaklimkin.persistence.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import me.nikitaklimkin.domain.expenses.Amount
import me.nikitaklimkin.domain.expenses.Expenses
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.model.DomainError
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import java.time.OffsetDateTime
import java.util.*

class ExpensesPersistenceModel(
    @BsonId
    override val id: Id<ExpensesPersistenceModel>,
    private val name: String,
    private val amount: Double,
    private val type: String,
    private val description: String?,
    private val userId: Id<UserPersistenceModel>,
    private val created: OffsetDateTime
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(expenses: Expenses): ExpensesPersistenceModel {
            return ExpensesPersistenceModel(
                expenses.id.toString().toId(),
                expenses.name.toStringValue(),
                expenses.amount.toDoubleValue(),
                expenses.type.toStringValue(),
                expenses.description,
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
                        id.toExpensesId(),
                        currentAmount,
                        type,
                        description,
                        userId.toUserId(),
                        created
                    )
                }
        }
    }
}

fun ExpensesId.toPersistenceId(): Id<ExpensesPersistenceModel> {
    return this.toString().toId()
}

fun Id<ExpensesPersistenceModel>.toExpensesId(): ExpensesId {
    return ExpensesId(UUID.fromString(this.toString()))
}