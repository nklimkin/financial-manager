package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.user.UserId

class CardAccount internal constructor(
    id: AccountId,
    userId: UserId,
    private var balance: MoneyAmount,
    bankName: BankName,
    description: AccountDescription,
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
        if (data is UpdateCardAccount) {
            data.bankName?.let { this.bankName = it }
            data.description?.let { this.description = it }
            return Unit.right()
        } else {
            return UpdateAccountDomainError.InvalidRequest.left()
        }
    }

    companion object {

        fun build(
            id: AccountId,
            userId: UserId,
            balance: MoneyAmount,
            bankName: BankName,
            description: AccountDescription
        ): CardAccount {
            return CardAccount(
                id,
                userId,
                balance,
                bankName,
                description,
                true
            )
        }

        fun restore(
            id: AccountId,
            userId: UserId,
            balance: MoneyAmount,
            bankName: BankName,
            description: AccountDescription,
            active: Boolean
        ): CardAccount {
            return CardAccount(
                id,
                userId,
                balance,
                bankName,
                description,
                active
            )
        }

    }
}

class NewCardAccount(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    val balance: MoneyAmount
) : NewAccount(userId, bankName, description) {

    override fun buildAccount() = CardAccount.build(
        AccountId.init(),
        userId,
        balance,
        bankName,
        description
    )
}

class UpdateCardAccount(
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?,
) : UpdateAccount(accountId, bankName, description)