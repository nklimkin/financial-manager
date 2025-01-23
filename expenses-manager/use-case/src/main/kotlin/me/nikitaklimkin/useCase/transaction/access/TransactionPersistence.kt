package me.nikitaklimkin.useCase.transaction.access

import arrow.core.Either
import me.nikitaklimkin.domain.transaction.Transaction
import me.nikitaklimkin.model.DomainError

interface TransactionPersistence {

    fun save(transaction: Transaction): Either<TransactionPersistenceError.TransactionAlreadyExists, Unit>

    fun update(transaction: Transaction): Either<TransactionPersistenceError.TransactionNotFound, Unit>
}

sealed class TransactionPersistenceError : DomainError() {

    data object TransactionAlreadyExists : TransactionPersistenceError()

    data object TransactionNotFound : TransactionPersistenceError()

}