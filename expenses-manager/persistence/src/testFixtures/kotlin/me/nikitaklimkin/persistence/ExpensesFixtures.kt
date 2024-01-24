package me.nikitaklimkin.persistence

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.persistence.expenses.model.ExpensesPersistenceModel
import me.nikitaklimkin.persistence.user.model.UserPersistenceModel
import me.nikitaklimkin.persistence.expenses.model.toPersistenceId
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import org.litote.kmongo.Id
import java.time.OffsetDateTime

fun buildExpensesPersistenceModel(
    expensesId: Id<ExpensesPersistenceModel> = EXPENSES_ID.toPersistenceId(),
    userId: Id<UserPersistenceModel> = USER_ID.toPersistenceId(),
    name: String = VALID_NAME,
    amount: Double = VALID_AMOUNT,
    type: String = VALID_TYPE,
    description: String? = DESCRIPTION,
    created: OffsetDateTime = OffsetDateTime.now()
) = ExpensesPersistenceModel(
    expensesId,
    name,
    amount,
    type,
    description,
    userId,
    created
)