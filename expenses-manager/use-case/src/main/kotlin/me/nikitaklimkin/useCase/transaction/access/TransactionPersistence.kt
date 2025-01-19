package me.nikitaklimkin.useCase.transaction.access

import arrow.core.Either
import me.nikitaklimkin.domain.transaction.Transaction
import me.nikitaklimkin.model.DomainError

interface TransactionPersistence {

    fun save(transaction: Transaction): Either<PersistenceError.IllegalSaveRequest, Unit>
}

sealed class PersistenceError : DomainError() {

    data object IllegalSaveRequest : PersistenceError()

}