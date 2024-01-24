package me.nikitaklimkin.persistence.expenses.model

import arrow.core.*
import arrow.core.raise.either
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.expenses.*
import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.persistence.common.model.PersistenceModel
import me.nikitaklimkin.persistence.user.model.UserPersistenceModel
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import java.time.OffsetDateTime
import java.util.*

class ExpensesPersistenceModel(
    @BsonId
    override val id: Id<ExpensesPersistenceModel>,
    val name: String,
    val amount: Double,
    val type: String,
    val description: String?,
    val userId: Id<UserPersistenceModel>,
    val created: OffsetDateTime
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(expenses: ExpensesDto, userId: UserId): ExpensesPersistenceModel {
            return ExpensesPersistenceModel(
                expenses.id.toString().toId(),
                expenses.name.toStringValue(),
                expenses.amount.toDoubleValue(),
                expenses.type.toStringValue(),
                expenses.description,
                userId.toPersistenceId(),
                expenses.created
            )
        }

    }

    fun toBusiness(): Either<DomainError, Expenses> {
        val amountValue = Amount.from(amount)
        val expensesName = ExpensesName.from(name)
        val expensesType = ExpensesType.from(type)
        return either {
            val amount = amountValue.bind()
            val name = expensesName.bind()
            val type = expensesType.bind()
            Expenses(
                id.toExpensesId(),
                name,
                amount,
                type,
                description,
                created
            )
        }
    }
}

fun ExpensesId.toPersistenceId(): Id<ExpensesPersistenceModel> {
    return this.toString().toId()
}

fun Id<ExpensesPersistenceModel>.toExpensesId(): ExpensesId {
    return ExpensesId(UUID.fromString(this.toString()))
}