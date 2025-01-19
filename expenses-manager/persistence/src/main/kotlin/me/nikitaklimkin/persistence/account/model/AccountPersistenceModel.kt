package me.nikitaklimkin.persistence.account.model

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.*
import me.nikitaklimkin.persistence.common.model.PersistenceModel
import me.nikitaklimkin.persistence.user.model.UserPersistenceModel
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import me.nikitaklimkin.persistence.user.model.toUserId
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import java.math.BigDecimal
import java.time.OffsetDateTime

data class AccountPersistenceModel(
    @BsonId
    override val id: Id<AccountPersistenceModel>,
    val userId: Id<UserPersistenceModel>,
    val type: AccountPersistenceType,
    val bankName: String,
    val description: String,
    val balance: BigDecimal,
    val expectedBalance: BigDecimal?,
    val interest: Double?,
    val openedDate: OffsetDateTime?,
    val closedDate: OffsetDateTime?,
    val active: Boolean
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(account: Account): AccountPersistenceModel {
            return when (account) {
                is DepositAccount -> fromDeposit(account)
                is CardAccount -> fromCard(account)
                is PiggyBankAccount -> fromPiggy(account)
                is BrokerageAccount -> fromBrokerage(account)
            }
        }

        private fun fromDeposit(account: DepositAccount) =
            AccountPersistenceModel(
                account.id.toPersistenceId(),
                account.userId.toPersistenceId(),
                AccountPersistenceType.DEPOSIT,
                account.bankName.value,
                account.description.value,
                account.balance().value,
                account.expectedFinalBalance.value,
                account.interest.value,
                account.openedDate,
                account.closedDate,
                account.active
            )

        private fun fromCard(account: CardAccount) =
            AccountPersistenceModel(
                account.id.toPersistenceId(),
                account.userId.toPersistenceId(),
                AccountPersistenceType.CARD,
                account.bankName.value,
                account.description.value,
                account.balance().value,
                null,
                null,
                null,
                null,
                account.active
            )

        private fun fromPiggy(account: PiggyBankAccount) =
            AccountPersistenceModel(
                account.id.toPersistenceId(),
                account.userId.toPersistenceId(),
                AccountPersistenceType.PIGGY,
                account.bankName.value,
                account.description.value,
                account.balance().value,
                null,
                account.interest.value,
                null,
                null,
                account.active
            )

        private fun fromBrokerage(account: BrokerageAccount) =
            AccountPersistenceModel(
                account.id.toPersistenceId(),
                account.userId.toPersistenceId(),
                AccountPersistenceType.BROKERAGE,
                account.bankName.value,
                account.description.value,
                account.balance().value,
                null,
                null,
                null,
                null,
                account.active
            )
    }

    fun toBusiness(): Either<CreateAccountError, Account> {
        return when (this.type) {
            AccountPersistenceType.DEPOSIT -> toDepositBusiness()
            AccountPersistenceType.PIGGY -> toPiggyBusiness()
            AccountPersistenceType.CARD -> toCardBusiness()
            AccountPersistenceType.BROKERAGE -> toBrokerageBusiness()
        }
    }

    private fun toDepositBusiness(): Either<CreateAccountError, DepositAccount> {
        return either {
            val id = this@AccountPersistenceModel.id.toAccountId().bind()
            val bankName = BankName.from(this@AccountPersistenceModel.bankName).bind()
            val description = AccountDescription.from(this@AccountPersistenceModel.description).bind()
            val initialBalance = MoneyAmount.from(this@AccountPersistenceModel.balance)
            val expectedBalance = businessExpectedBalance().bind()
            val interest = businessInterest().bind()
            val openedDate = businessOpenedDate().bind()
            val closedDate = businessClosedDate().bind()
            DepositAccount.restore(
                id,
                this@AccountPersistenceModel.userId.toUserId(),
                bankName,
                description,
                initialBalance,
                expectedBalance,
                interest,
                openedDate,
                closedDate,
                this@AccountPersistenceModel.active
            )
        }
            .mapLeft { CreateAccountError }
    }

    private fun toPiggyBusiness(): Either<CreateAccountError, PiggyBankAccount> {
        return either {
            val id = this@AccountPersistenceModel.id.toAccountId().bind()
            val bankName = BankName.from(this@AccountPersistenceModel.bankName).bind()
            val description = AccountDescription.from(this@AccountPersistenceModel.description).bind()
            val initialBalance = MoneyAmount.from(this@AccountPersistenceModel.balance)
            val interest = businessInterest().bind()
            PiggyBankAccount.restore(
                id,
                this@AccountPersistenceModel.userId.toUserId(),
                initialBalance,
                bankName,
                description,
                interest,
                this@AccountPersistenceModel.active
            )
        }
            .mapLeft { CreateAccountError }
    }

    private fun toCardBusiness(): Either<CreateAccountError, CardAccount> {
        return either {
            val id = this@AccountPersistenceModel.id.toAccountId().bind()
            val bankName = BankName.from(this@AccountPersistenceModel.bankName).bind()
            val description = AccountDescription.from(this@AccountPersistenceModel.description).bind()
            val initialBalance = MoneyAmount.from(this@AccountPersistenceModel.balance)
            CardAccount.restore(
                id,
                this@AccountPersistenceModel.userId.toUserId(),
                initialBalance,
                bankName,
                description,
                this@AccountPersistenceModel.active
            )
        }
            .mapLeft { CreateAccountError }
    }

    private fun toBrokerageBusiness(): Either<CreateAccountError, BrokerageAccount> {
        return either {
            val id = this@AccountPersistenceModel.id.toAccountId().bind()
            val bankName = BankName.from(this@AccountPersistenceModel.bankName).bind()
            val description = AccountDescription.from(this@AccountPersistenceModel.description).bind()
            val initialBalance = MoneyAmount.from(this@AccountPersistenceModel.balance)
            BrokerageAccount.restore(
                id,
                this@AccountPersistenceModel.userId.toUserId(),
                initialBalance,
                bankName,
                description,
                this@AccountPersistenceModel.active
            )
        }
            .mapLeft { CreateAccountError }
    }

    private fun businessExpectedBalance(): Either<CreateAccountError, MoneyAmount> {
        val possibleExpectedBalance = this@AccountPersistenceModel.expectedBalance
            ?.let { MoneyAmount.from(it) }
        return possibleExpectedBalance?.right() ?: CreateAccountError.left()
    }

    private fun businessInterest(): Either<CreateAccountError, Interest> {
        val possibleInterest = this@AccountPersistenceModel.interest
            ?.let { Interest.from(it) }
        return possibleInterest
            ?.mapLeft { CreateAccountError }
            ?: CreateAccountError.left()
    }

    private fun businessOpenedDate(): Either<CreateAccountError, OffsetDateTime> {
        return this@AccountPersistenceModel.openedDate?.right() ?: CreateAccountError.left()
    }

    private fun businessClosedDate(): Either<CreateAccountError, OffsetDateTime> {
        return this@AccountPersistenceModel.closedDate?.right() ?: CreateAccountError.left()
    }

}

enum class AccountPersistenceType {
    DEPOSIT,
    PIGGY,
    CARD,
    BROKERAGE


}

fun Id<AccountPersistenceModel>.toAccountId(): Either<CreateAccountIdError, AccountId> {
    return AccountId.from(this.toString())
}

fun AccountId.toPersistenceId(): Id<AccountPersistenceModel> {
    return this.toUuid().toString().toId()
}