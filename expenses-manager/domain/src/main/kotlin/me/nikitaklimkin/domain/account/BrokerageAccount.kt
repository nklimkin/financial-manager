package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.user.UserId

class BrokerageAccount internal constructor(
    id: AccountId,
    userId: UserId,
    private var freeBalance: MoneyAmount,
    bankName: BankName,
    description: AccountDescription,
    active: Boolean
) : Account(id, userId, bankName, description, active) {
    override fun top(amount: MoneyAmount): Either<IllegalAccountActionError, Unit> {
        freeBalance += amount;
        return Unit.right()
    }

    override fun withdraw(amount: MoneyAmount): Either<IllegalAccountActionError, Unit> {
        if (freeBalance >= amount) {
            freeBalance -= amount
            return Unit.right()
        } else {
            return IllegalAccountActionError.IllegalWithdraw.left()
        }
    }

    override fun balance() = freeBalance
    override fun updateInfo(data: UpdateAccount): Either<UpdateAccountDomainError, Unit> {
        if (data is UpdateBrokerAccount) {
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
        ): BrokerageAccount {
            return BrokerageAccount(
                id,
                userId,
                balance,
                bankName,
                description,
                true
            )
        }
    }
}

class NewBrokerAccount(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    val initBalance: MoneyAmount
) : NewAccount(userId, bankName, description) {

    override fun buildAccount() = BrokerageAccount.build(
        AccountId.init(),
        userId,
        initBalance,
        bankName,
        description
    )
}

class UpdateBrokerAccount(
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?,
) : UpdateAccount(accountId, bankName, description)