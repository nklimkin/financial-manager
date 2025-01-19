package me.nikitaklimkin.useCase.transaction.impl

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.transaction.Transaction
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.transaction.UpdateTransaction
import me.nikitaklimkin.useCase.transaction.UpdateTransactionDTO
import me.nikitaklimkin.useCase.transaction.UpdateTransactionError
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import me.nikitaklimkin.useCase.transaction.toDomainDto
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class UpdateTransactionUseCase(
    private val accountExtractor: AccountExtractor,
    private val transactionExtractor: TransactionExtractor,
    private val transactionPersistence: TransactionPersistence
) : UpdateTransaction {
    override fun execute(request: UpdateTransactionDTO): Either<UpdateTransactionError, Unit> {
        log.debug { "Execute update transaction with id = [${request.id}]" }
        log.trace { "Execute update transaction use case = [$request]" }
        val transaction = transactionExtractor.findById(request.id)
        return if (transaction == null) {
            UpdateTransactionError.TransactionNotFound.left()
        } else {
            updateTransaction(transaction, request)
        }
    }

    private fun updateTransaction(
        transaction: Transaction,
        request: UpdateTransactionDTO
    ): Either<UpdateTransactionError.TransactionNotFound, Unit> {
        val account = accountExtractor.findById(transaction.accountId)
        if (account == null || !account.canBeProcessByUser(request.userId)) {
            return UpdateTransactionError.TransactionNotFound.left()
        } else {
            transaction.update(request.toDomainDto())
            transactionPersistence.save(transaction)
            return Unit.right()
        }
    }
}