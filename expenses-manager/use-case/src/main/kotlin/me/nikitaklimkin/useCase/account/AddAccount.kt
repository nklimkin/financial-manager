package me.nikitaklimkin.useCase.account

import arrow.core.Either
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.*
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainError
import java.time.OffsetDateTime

interface AddAccount {

    fun addBrokerAccount(request: AddNewBrokerAccountDTO): Either<AddNewAccountError, Unit>

    fun addCardAccount(request: AddNewCardAccountDTO): Either<AddNewAccountError, Unit>

    fun addDepositAccount(request: AddNewDepositAccountDTO): Either<AddNewAccountError, Unit>

    fun addPiggyAccount(request: AddNewPiggyAccountDTO): Either<AddNewAccountError, Unit>
}

class AddNewBrokerAccountDTO(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    private val initBalance: MoneyAmount
) : AddNewAccountDTO(userId, bankName, description) {

    override fun toDomainDto(): NewBrokerAccount {
        return NewBrokerAccount(userId, bankName, description, initBalance)
    }
}

class AddNewCardAccountDTO(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    private val initBalance: MoneyAmount
) : AddNewAccountDTO(userId, bankName, description) {

    override fun toDomainDto(): NewCardAccount {
        return NewCardAccount(userId, bankName, description, initBalance);
    }
}

class AddNewDepositAccountDTO(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    private val initialBalance: MoneyAmount,
    private val expectedFinalBalance: MoneyAmount,
    private val openedDate: OffsetDateTime,
    private val closedDate: OffsetDateTime,
    private val interest: Interest,
) : AddNewAccountDTO(userId, bankName, description) {

    override fun toDomainDto(): NewDepositAccount {
        return NewDepositAccount(
            userId,
            bankName,
            description,
            initialBalance,
            expectedFinalBalance,
            interest,
            openedDate,
            closedDate
        )
    }
}

class AddNewPiggyAccountDTO(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    private val initialBalance: MoneyAmount,
    private val interest: Interest
) : AddNewAccountDTO(userId, bankName, description) {

    override fun toDomainDto(): NewPiggyAccount {
        return NewPiggyAccount(
            userId,
            bankName,
            description,
            initialBalance,
            interest
        )
    }
}

sealed class AddNewAccountDTO(
    val userId: UserId,
    val bankName: BankName,
    val description: AccountDescription
) {

    abstract fun toDomainDto(): NewAccount

}

sealed class AddNewAccountError : DomainError() {

    data object UserNotFound : AddNewAccountError()

    data object AccountAlreadyExists : AddNewAccountError()

}
