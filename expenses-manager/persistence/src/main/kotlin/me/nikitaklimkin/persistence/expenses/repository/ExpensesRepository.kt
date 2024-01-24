package me.nikitaklimkin.persistence.expenses.repository

import arrow.core.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.expenses.Expenses
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.UserExpenses
import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.domain.expenses.event.SaveExpensesEvent
import me.nikitaklimkin.domain.expenses.event.UpdateExpensesEvent
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.expenses.model.ExpensesPersistenceModel
import me.nikitaklimkin.persistence.expenses.model.toPersistenceId
import me.nikitaklimkin.persistence.common.repository.AbstractRepository
import me.nikitaklimkin.persistence.common.repository.PersistError
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import mu.KotlinLogging
import org.litote.kmongo.eq
import org.litote.kmongo.getCollectionOfName

private const val EXPENSES_COLLECTION = "expenses"

private val log = KotlinLogging.logger { }

class ExpensesRepository(
    mongoClient: MongoClient,
    properties: DataBaseProperties
) : AbstractRepository<ExpensesPersistenceModel> {

    override lateinit var col: MongoCollection<ExpensesPersistenceModel>

    init {
        val dataBase = mongoClient.getDatabase(properties.dataBaseName)
        col = dataBase.getCollectionOfName(EXPENSES_COLLECTION)
    }

    fun save(userExpenses: UserExpenses): Either<PersistError, Unit> {
        log.debug { "Save expenses for user = [${userExpenses.id}]" }
        log.trace { "Save expenses = [$userExpenses]" }
        val idOfExpensesToSave = userExpenses.popEvents()
            .filterIsInstance(SaveExpensesEvent::class.java)
            .map { it.expensesId }
        val expensesToPersist = userExpenses.getExpenses()
            .filter { idOfExpensesToSave.contains(it.id) }
            .map { ExpensesPersistenceModel.fromBusiness(it, userExpenses.id) }
        return addAll(expensesToPersist)
            .map { Unit }
    }

    fun update(userExpenses: UserExpenses): Either<PersistError, Unit> {
        log.debug { "Update expenses for user = [${userExpenses.id}]" }
        log.trace { "Update expenses = [$userExpenses]" }
        val idOfExpensesToUpdate = userExpenses.popEvents()
            .filterIsInstance(UpdateExpensesEvent::class.java)
            .map { it.expensesId }
        val expensesToUpdate = userExpenses.getExpenses()
            .filter { idOfExpensesToUpdate.contains(it.id) }
            .map { ExpensesPersistenceModel.fromBusiness(it, userExpenses.id) }
        return updateAll(expensesToUpdate)
            .map { Unit }
    }

    fun getUserExpenses(userId: UserId): Either<DomainError, UserExpenses> {
        log.debug { "Get expenses for user = [$userId]" }
        return col
            .find(ExpensesPersistenceModel::userId eq userId.toPersistenceId())
            .toNonEmptyListOrNone()
            .map { buildUserExpensesFromPersistenceData(userId, it) }
            .getOrElse { ExpensesNotFoundError.left() }
    }

    private fun buildUserExpensesFromPersistenceData(
        userId: UserId,
        persistenceData: NonEmptyList<ExpensesPersistenceModel>
    ): Either<DomainError, UserExpenses> {
        val expensesList = mutableListOf<Expenses>()

        for (persistedExpenses in persistenceData) {
            val expenses = persistedExpenses.toBusiness()
            if (expenses.isLeft()) {
                return expenses.leftOrNull()!!.left()
            } else {
                expensesList.add(expenses.getOrNull()!!)
            }
        }
        return UserExpenses(userId, expensesList).right()
    }

    fun delete(id: ExpensesId): Either<DomainError, ExpensesDto> {
        log.debug { "Delete expenses with id = [$id]" }
        return delete(id.toPersistenceId())
            .flatMap { it.toBusiness() }
            .map { it.toDto() }
    }

}

object ExpensesNotFoundError : DomainError()