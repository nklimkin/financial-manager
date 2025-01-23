package me.nikitaklimkin.useCase.account.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.account.Account
import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.useCase.account.RemoveAccount
import me.nikitaklimkin.useCase.account.RemoveAccountError
import me.nikitaklimkin.useCase.account.RemoveAccountRequestDto
import me.nikitaklimkin.useCase.account.UpdateAccountError
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.user.access.UserExtractor
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class RemoveAccountImpl(
    private val userExtractor: UserExtractor,
    private val accountExtractor: AccountExtractor,
    private val accountPersistence: AccountPersistence
) : RemoveAccount {
    override fun execute(request: RemoveAccountRequestDto): Either<RemoveAccountError, Unit> {
        log.info { "Remove account with id = [${request.accountId}] for user  = [${request.userId}]" }
        val user = userExtractor.findByUserId(request.userId)?.right() ?: RemoveAccountError.UserNotFound.left()
        return user
            .mapLeft { _ -> RemoveAccountError.UserNotFound }
            .flatMap { currentUser -> removeAccount(currentUser, request.toDomainDto()) }
            .flatMap { account ->
                accountPersistence.update(account)
                    .mapLeft { _ -> RemoveAccountError.AccountNotFound }
            }
    }

    private fun removeAccount(
        user: User,
        request: me.nikitaklimkin.domain.account.RemoveAccount
    ): Either<RemoveAccountError, Account> {
        val userAccounts = accountExtractor.findByUser(user)
        if (userAccounts.isEmpty()) {
            return RemoveAccountError.AccountNotFound.left()
        }
        val matchAccount = userAccounts.firstOrNull { it.id == request.accountId }
        if (matchAccount == null) {
            return RemoveAccountError.AccountNotFound.left()
        } else {
            matchAccount.deactivate()
            return matchAccount.right()
        }
    }
}