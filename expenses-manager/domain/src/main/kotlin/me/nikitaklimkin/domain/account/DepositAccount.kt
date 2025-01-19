package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.user.UserId
import java.time.OffsetDateTime

class DepositAccount internal constructor(
    id: AccountId,
    userId: UserId,
    val initialBalance: MoneyAmount,
    val expectedFinalBalance: MoneyAmount,
    var openedDate: OffsetDateTime,
    var closedDate: OffsetDateTime,
    var interest: Interest,
    bankName: BankName,
    description: AccountDescription,
    active: Boolean
) : Account(id, userId, bankName, description, active) {
    override fun top(amount: MoneyAmount): Either<IllegalAccountActionError, Unit> {
        return IllegalAccountActionError.IllegalTop.left()
    }

    override fun withdraw(amount: MoneyAmount): Either<IllegalAccountActionError, Unit> {
        return IllegalAccountActionError.IllegalWithdraw.left()
    }

    override fun balance(): MoneyAmount = initialBalance

    override fun updateInfo(data: UpdateAccount): Either<UpdateAccountDomainError, Unit> {
        if (data is UpdateDepositAccount) {
            data.bankName?.let { this.bankName = it }
            data.description?.let { this.description = it }
            data.closedDate?.let { this.closedDate = it }
            data.openedDate?.let { this.openedDate = it }
            data.interest?.let { this.interest = it }
            return Unit.right()
        } else {
            return UpdateAccountDomainError.InvalidRequest.left()
        }
    }

    companion object {

        fun build(
            id: AccountId,
            userId: UserId,
            bankName: BankName,
            description: AccountDescription,
            initialBalance: MoneyAmount,
            expectedFinalBalance: MoneyAmount,
            interest: Interest,
            openedDate: OffsetDateTime,
            closedDate: OffsetDateTime
        ): DepositAccount {
            return DepositAccount(
                id,
                userId,
                initialBalance,
                expectedFinalBalance,
                openedDate,
                closedDate,
                interest,
                bankName,
                description,
                true
            )
        }

        fun restore(
            id: AccountId,
            userId: UserId,
            bankName: BankName,
            description: AccountDescription,
            initialBalance: MoneyAmount,
            expectedFinalBalance: MoneyAmount,
            interest: Interest,
            openedDate: OffsetDateTime,
            closedDate: OffsetDateTime,
            active: Boolean
        ): DepositAccount {
            return DepositAccount(
                id,
                userId,
                initialBalance,
                expectedFinalBalance,
                openedDate,
                closedDate,
                interest,
                bankName,
                description,
                active
            )
        }
    }
}

class NewDepositAccount(
    userId: UserId,
    bankName: BankName,
    description: AccountDescription,
    val initialBalance: MoneyAmount,
    val expectedFinalBalance: MoneyAmount,
    val interest: Interest,
    val openedDate: OffsetDateTime,
    val closedDate: OffsetDateTime
) : NewAccount(userId, bankName, description) {
    override fun buildAccount() = DepositAccount.build(
        AccountId.init(),
        userId,
        bankName,
        description,
        initialBalance,
        expectedFinalBalance,
        interest,
        openedDate,
        closedDate,
    )
}

class UpdateDepositAccount(
    accountId: AccountId,
    bankName: BankName?,
    description: AccountDescription?,
    val interest: Interest?,
    val openedDate: OffsetDateTime?,
    val closedDate: OffsetDateTime?
) : UpdateAccount(accountId, bankName, description)