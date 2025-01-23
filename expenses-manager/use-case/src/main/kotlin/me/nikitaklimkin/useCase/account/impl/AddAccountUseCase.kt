package me.nikitaklimkin.useCase.account.impl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.account.AccountIdGenerator
import me.nikitaklimkin.useCase.account.*
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.user.access.UserExtractor
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class AddAccountUseCase(
    private val userExtractor: UserExtractor,
    private val accountPersistence: AccountPersistence,
    private val accountIdGenerator: AccountIdGenerator
) : AddAccount {
    override fun addBrokerAccount(request: AddNewBrokerAccountDTO): Either<AddNewAccountError, Unit> {
        log.info {
            "Add broker account for user with id = [${request.userId}] " +
                    "and account with bank name = [${request.bankName}]  "
        }
        return addAccount(request)
    }

    override fun addCardAccount(request: AddNewCardAccountDTO): Either<AddNewAccountError, Unit> {
        log.info {
            "Add card account for user with id = [${request.userId}] " +
                    "and account with bank name = [${request.bankName}]  "
        }
        return addAccount(request)
    }

    override fun addDepositAccount(request: AddNewDepositAccountDTO): Either<AddNewAccountError, Unit> {
        log.info {
            "Add deposit account for user with id = [${request.userId}] " +
                    "and account with bank name = [${request.bankName}]  "
        }
        return addAccount(request)
    }

    override fun addPiggyAccount(request: AddNewPiggyAccountDTO): Either<AddNewAccountError, Unit> {
        log.info {
            "Add piggy account for user with id = [${request.userId}] " +
                    "and account with bank name = [${request.bankName}]  "
        }
        return addAccount(request)
    }

    private fun addAccount(request: AddNewAccountDTO): Either<AddNewAccountError, Unit> {
        val user = userExtractor.findByUserId(request.userId)?.right() ?: AddNewAccountError.UserNotFound.left()
        return user
            .map { _ -> request.toDomainDto().buildAccount(accountIdGenerator) }
            .flatMap { userAccounts ->
                accountPersistence.save(userAccounts)
                    .mapLeft { _ -> AddNewAccountError.AccountAlreadyExists }
            }
    }
}