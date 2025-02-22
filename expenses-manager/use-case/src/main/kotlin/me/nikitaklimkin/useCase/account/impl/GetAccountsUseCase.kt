package me.nikitaklimkin.useCase.account.impl

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.useCase.account.*
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.user.access.UserExtractor
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class GetAccountsUseCase(
    private val userExtractor: UserExtractor,
    private val accountExtractor: AccountExtractor
) : GetAccounts {


    override fun execute(request: GetAccountRequest): Either<GetAccountError, GetAccountResponse> {
        log.info { "Get accounts for user = [{${request.userId}}]" }
        val user = userExtractor.findByUserId(request.userId)?.right() ?: GetAccountError.UserNotFound.left()
        return user
            .mapLeft { _ -> GetAccountError.UserNotFound }
            .map { currentUser ->
                val accountsDTO = accountExtractor.findByUser(currentUser)
                    .map { account ->
                        val summary = account.summary()
                        AccountDto(summary.accountId, summary.bankName, summary.description)
                    }
                GetAccountResponse(request.userId, accountsDTO)
            }
    }
}