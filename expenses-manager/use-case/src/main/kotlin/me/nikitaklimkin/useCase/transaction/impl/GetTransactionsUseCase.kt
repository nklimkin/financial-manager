package me.nikitaklimkin.useCase.transaction.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.transaction.dto.TransactionDTO
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.transaction.GetTransactions
import me.nikitaklimkin.useCase.transaction.GetTransactionsDTO
import me.nikitaklimkin.useCase.transaction.GetTransactionsError
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.user.access.UserExtractor
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class GetTransactionsUseCase(
    private val userExtractor: UserExtractor,
    private val accountExtractor: AccountExtractor,
    private val transactionExtractor: TransactionExtractor
) : GetTransactions {
    override fun execute(request: GetTransactionsDTO): Either<GetTransactionsError, Collection<TransactionDTO>> {
        log.debug { "Execute get transaction for userId = [${request.userId}] and accountId = [${request.accountId}]" }
        val user =
            userExtractor.findByUserId(request.userId)?.right() ?: GetTransactionsError.TransactionsNotFound.left()
        return user
            .mapLeft { GetTransactionsError.TransactionsNotFound }
            .flatMap { currentUser ->
                accountExtractor.findByUser(currentUser)
                    ?.firstOrNull { it.id == request.accountId }
                    ?.right() ?: GetTransactionsError.TransactionsNotFound.left()
            }
            .flatMap { account ->
                val transactions = transactionExtractor.findByAccount(account)?.map { it.summary() } ?: listOf()
                transactions.right()
            }
    }
}