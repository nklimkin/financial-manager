package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.user.UserId

class PiggyBankAccount internal constructor(
    id: AccountId,
    userId: UserId,
    private var balance: MoneyAmount,
    bankName: BankName,
    description: AccountDescription,
    var interest: Interest,
    active: Boolean
) : Account(id, userId, bankName, description, active) {

    override fun top(amount: MoneyAmount): Either<IllegalAccountActionError, Unit> {
        this.balance += amount;
        return Unit.right()
    }

    override fun withdraw(amount: MoneyAmount): Either<IllegalAccountActionError, Unit> {
        if (this.balance >= amount) {
            this.balance -= amount
            return Unit.right()
        } else {
            return IllegalAccountActionError.IllegalWithdraw.left()
        }
    }

    override fun balance() = balance

    override fun updateInfo(data: UpdateAccount): Either<UpdateAccountDomainError, Unit> {
        if (data is UpdatePiggyAccount) {
            data.bankName?.let { this.bankName = it }
            data.description?.let { this.description = it }
            data.interest?.let { this.interest = it }
            return Unit.right()
        } else {
            return UpdateAccountDomainError.InvalidRequest.left()
        }
    }

    companion object {

        fun build(
            idGenerator: AccountIdGenerator,
            userId: UserId,
            balance: MoneyAmount,
            bankName: BankName,
            description: AccountDescription,
            interest: Interest
        ): PiggyBankAccount {
            return PiggyBankAccount(
                idGenerator.generate(),
                userId,
                balance,
                bankName,
                description,
                interest,
                true
            )
        }

        fun restore(
            id: AccountId,
            userId: UserId,
            balance: MoneyAmount,
            bankName: BankName,
            description: AccountDescription,
            interest: Interest,
            active: Boolean
        ): PiggyBankAccount {
            return PiggyBankAccount(
                id,
                userId,
                balance,
                bankName,
                description,
                interest,
                active
            )
        }

    }
}

class NewPiggyAccount(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    val balance: MoneyAmount,
    val interest: Interest
) : NewAccount(userId, bankName, description) {
    override fun buildAccount(idGenerator: AccountIdGenerator) = PiggyBankAccount.build(
        idGenerator,
        userId,
        balance,
        bankName,
        description,
        interest
    )
}

class UpdatePiggyAccount(
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?,
    val interest: Interest?
) : UpdateAccount(accountId, bankName, description)