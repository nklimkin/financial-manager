package me.nikitaklimkin.domain.expenses

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.domain.expenses.dto.SaveExpensesDto
import me.nikitaklimkin.domain.expenses.dto.UpdateExpensesDto
import me.nikitaklimkin.domain.expenses.event.SaveExpensesEvent
import me.nikitaklimkin.domain.expenses.event.UpdateExpensesEvent
import me.nikitaklimkin.model.AggregateRoot
import me.nikitaklimkin.model.DomainError

class UserExpenses(
    val id: UserId,
    private val expensesList: MutableCollection<Expenses>
) : AggregateRoot<UserId>(id) {

    companion object {

        fun buildNew(id: UserId): UserExpenses {
            return UserExpenses(
                id = id,
                mutableListOf()
            )
        }

    }

    fun addExpenses(
        expensesIdGenerator: ExpensesIdGenerator,
        newExpenses: SaveExpensesDto
    ): ExpensesDto {
        val expenses = Expenses(
            expensesIdGenerator.generate(),
            newExpenses.name,
            newExpenses.amount,
            newExpenses.type,
            newExpenses.description,
            newExpenses.created
        )
        expensesList.add(expenses)
        addEvent(SaveExpensesEvent(expenses.id))
        return expenses.toDto()
    }

    fun updateExpenses(updatedExpenses: UpdateExpensesDto): Either<ExpensesNotFoundError, ExpensesDto> {
        val currentExpenses = expensesList.find { it.id == updatedExpenses.id }
        return if (currentExpenses == null) {
            ExpensesNotFoundError.left()
        } else {
            updateExpensesByNewData(currentExpenses, updatedExpenses)
            addEvent(UpdateExpensesEvent(updatedExpenses.id))
            currentExpenses.toDto().right()
        }
    }

    private fun updateExpensesByNewData(expensesToUpdate: Expenses, updatedData: UpdateExpensesDto) {
        updatedData.name?.let { expensesToUpdate.name = it }
        updatedData.amount?.let { expensesToUpdate.amount = it }
        updatedData.type?.let { expensesToUpdate.type = it }
        updatedData.description?.let { expensesToUpdate.description = it }
        updatedData.created?.let { expensesToUpdate.created = it }
    }

    fun deleteExpenses(id: ExpensesId): Either<ExpensesNotFoundError, ExpensesDto> {
        val expensesToDelete = expensesList.find { it.id == id }
        return if (expensesToDelete == null) {
            ExpensesNotFoundError.left()
        } else {
            expensesList.remove(expensesToDelete)
            expensesToDelete.toDto().right()
        }
    }

    fun getExpenses(): List<ExpensesDto> {
        return expensesList.map { it.toDto() }
    }
}

object ExpensesNotFoundError : DomainError()