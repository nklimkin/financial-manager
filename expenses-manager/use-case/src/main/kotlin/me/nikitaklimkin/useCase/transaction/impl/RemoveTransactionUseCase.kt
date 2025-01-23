package me.nikitaklimkin.useCase.transaction.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.transaction.Transaction
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.transaction.DeleteTransactionDTO
import me.nikitaklimkin.useCase.transaction.RemoveTransaction
import me.nikitaklimkin.useCase.transaction.RemoveTransactionError
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class RemoveTransactionUseCase(
    private val accountExtractor: AccountExtractor,
    private val transactionExtractor: TransactionExtractor,
    private val transactionPersistence: TransactionPersistence
) : RemoveTransaction {
    override fun execute(request: DeleteTransactionDTO): Either<RemoveTransactionError, Unit> {
        log.debug { "Execute delete transaction with id = [${request.transactionId}]" }
        return deactivateTransaction(request)
            .flatMap {
                transactionPersistence.update(it)
                    .mapLeft { _ -> RemoveTransactionError.TransactionNotFound }
            }
    }

    private fun deactivateTransaction(
        request: DeleteTransactionDTO
    ): Either<RemoveTransactionError, Transaction> {
        val transactionToRemove = transactionExtractor.findById(request.transactionId)
        return if (transactionToRemove == null) {
            RemoveTransactionError.TransactionNotFound.left()
        } else {
            deactivateTransaction(transactionToRemove, request)
        }
    }

    private fun deactivateTransaction(
        transactionToRemove: Transaction,
        request: DeleteTransactionDTO
    ): Either<RemoveTransactionError.TransactionNotFound, Transaction> {
        val accountId = transactionToRemove.accountId
        val relatedAccount = accountExtractor.findById(accountId)
        if (relatedAccount == null || !relatedAccount.canBeProcessByUser(request.userId)) {
            return RemoveTransactionError.TransactionNotFound.left()
        } else {
            transactionToRemove.deactivate()
            return transactionToRemove.right()
        }
    }
}