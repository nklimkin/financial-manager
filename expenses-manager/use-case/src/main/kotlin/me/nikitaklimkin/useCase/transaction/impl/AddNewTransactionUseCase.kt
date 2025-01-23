package me.nikitaklimkin.useCase.transaction.impl

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.transaction.Transaction
import me.nikitaklimkin.domain.transaction.TransactionIdGenerator
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.transaction.AddNewTransactionDTO
import me.nikitaklimkin.useCase.transaction.AddNewTransaction
import me.nikitaklimkin.useCase.transaction.AddNewTransactionError
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class AddNewTransactionUseCase(
    private val transactionPersistence: TransactionPersistence,
    private val transactionIdGenerator: TransactionIdGenerator,
    private val accountExtractor: AccountExtractor
) : AddNewTransaction {
    override fun execute(request: AddNewTransactionDTO): Either<AddNewTransactionError, Unit> {
        log.debug { "Execute add new transaction use case for accountId = [${request.accountId}]" }
        log.trace { "Execute add new transaction use case = [$request]" }
        val accountExists = accountExtractor.isAccountExists(request.accountId)
        if (!accountExists) {
            return AddNewTransactionError.AccountNotFound.left()
        }
        val transaction = Transaction.build(
            transactionIdGenerator,
            request.accountId,
            request.name,
            request.amount,
            request.type,
            request.direction,
            request.description
        )
        return transactionPersistence.save(transaction)
            .mapLeft { _ -> AddNewTransactionError.TransactionAlreadyExists }
    }

}