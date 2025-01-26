package me.nikitaklimkin.useCase.account.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.account.Account
import me.nikitaklimkin.domain.account.UpdateAccountDomainError
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.useCase.account.UpdateAccount
import me.nikitaklimkin.useCase.account.UpdateAccountError
import me.nikitaklimkin.useCase.account.UpdateAccountRequest
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.user.access.UserExtractor
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class UpdateAccountUseCase(
    private val userExtractor: UserExtractor,
    private val accountExtractor: AccountExtractor,
    private val accountPersistence: AccountPersistence
) : UpdateAccount {
    override fun execute(request: UpdateAccountRequest): Either<UpdateAccountError, Unit> {
        log.debug { "Update account with id = [{${request.accountId}}] for user = [{${request.userId}}]" }
        val user = userExtractor.findByUserId(request.userId)?.right() ?: UpdateAccountError.UserNotFound.left()
        return user
            .mapLeft { _ -> UpdateAccountError.UserNotFound }
            .flatMap { currentUser -> updateAccount(currentUser, request) }
            .flatMap { account ->
                accountPersistence.update(account)
                    .mapLeft { _ -> UpdateAccountError.AccountNotFound }
            }
    }

    private fun updateAccount(user: User, request: UpdateAccountRequest): Either<UpdateAccountError, Account> {
        val userAccounts = accountExtractor.findByUser(user)
        if (userAccounts.isEmpty()) {
            return UpdateAccountError.AccountNotFound.left()
        }
        val account = userAccounts.firstOrNull() { it.id == request.accountId }
        return account
            ?.updateInfo(request.toDomainDto())
            ?.map { _ -> account }
            ?.mapLeft { mapDomainErrorToUseCaseError(it) }
            ?: UpdateAccountError.AccountNotFound.left()
    }

    private fun mapDomainErrorToUseCaseError(error: UpdateAccountDomainError): UpdateAccountError {
        return when (error) {
            UpdateAccountDomainError.InvalidRequest -> UpdateAccountError.InvalidRequest
            UpdateAccountDomainError.AccountNotFound -> UpdateAccountError.AccountNotFound
        }
    }
}