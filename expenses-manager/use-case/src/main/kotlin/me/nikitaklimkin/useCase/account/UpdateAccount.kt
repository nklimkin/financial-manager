package me.nikitaklimkin.useCase.account

import arrow.core.Either
import me.nikitaklimkin.domain.account.*
import me.nikitaklimkin.domain.account.UpdateAccount
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainError
import java.time.OffsetDateTime

interface UpdateAccount {

    fun execute(request: UpdateAccountRequest): Either<UpdateAccountError, Unit>

}

class UpdateBrokerAccountRequest(
    userId: UserId,
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?,
) : UpdateAccountRequest(userId, accountId, bankName, description) {

    override fun toDomainDto(): UpdateBrokerAccount {
        return UpdateBrokerAccount(accountId, bankName, description)
    }
}

class UpdateCardAccountRequest(
    userId: UserId,
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?
) : UpdateAccountRequest(userId, accountId, bankName, description) {

    override fun toDomainDto(): UpdateCardAccount {
        return UpdateCardAccount(accountId, bankName, description);
    }
}

class UpdateDepositAccountRequest(
    userId: UserId,
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?,
    private val interest: Interest?,
    private val openedDate: OffsetDateTime?,
    private val closedDate: OffsetDateTime?
) : UpdateAccountRequest(userId, accountId, bankName, description) {

    override fun toDomainDto(): UpdateDepositAccount {
        return UpdateDepositAccount(
            accountId,
            bankName,
            description,
            interest,
            openedDate,
            closedDate
        )
    }
}

class UpdatePiggyAccountRequest(
    userId: UserId,
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?,
    private val interest: Interest?
) : UpdateAccountRequest(userId, accountId, bankName, description) {

    override fun toDomainDto(): UpdatePiggyAccount {
        return UpdatePiggyAccount(
            accountId,
            bankName,
            description,
            interest
        )
    }
}

sealed class UpdateAccountRequest(
    val userId: UserId,
    val accountId: AccountId,
    val bankName: BankName?,
    val description: AccountDescription?
) {

    abstract fun toDomainDto(): UpdateAccount

}

sealed class UpdateAccountError : DomainError() {

    data object UserNotFound : UpdateAccountError()
    data object AccountNotFound : UpdateAccountError()

    data object InvalidRequest : UpdateAccountError()

}