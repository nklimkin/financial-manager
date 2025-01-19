package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainEntity
import me.nikitaklimkin.model.DomainError
import java.util.*

sealed class Account(
    val id: AccountId,
    val userId: UserId,
    var bankName: BankName,
    var description: AccountDescription,
    var active: Boolean = true
) : DomainEntity<AccountId>(id) {

    abstract fun top(amount: MoneyAmount): Either<IllegalAccountActionError, Unit>

    abstract fun withdraw(amount: MoneyAmount): Either<IllegalAccountActionError, Unit>

    abstract fun balance(): MoneyAmount

    abstract fun updateInfo(data: UpdateAccount): Either<UpdateAccountDomainError, Unit>

    fun summary(): AccountSummary = AccountSummary(id, bankName, description)

    fun deactivate() {
        active = false
    }

    fun canBeProcessByUser(userId: UserId): Boolean = this.userId == userId

}

data class AccountId(private val uuid: UUID) {

    companion object {

        fun init(): AccountId {
            return AccountId(UUID.randomUUID())
        }

        fun from(value: String): Either<CreateAccountIdError, AccountId> {
            return try {
                AccountId(UUID.fromString(value)).right()
            } catch (exception: IllegalArgumentException) {
                CreateAccountIdError.left()
            }
        }

    }

    fun toUuid() = uuid;

}

data object CreateAccountIdError : DomainError()

data object CreateAccountError : DomainError()

sealed class IllegalAccountActionError : DomainError() {

    data object IllegalTop : IllegalAccountActionError()

    data object IllegalWithdraw : IllegalAccountActionError()

}

sealed class NewAccount(
    val userId: UserId,
    val bankName: BankName,
    val description: AccountDescription
) {

    abstract fun buildAccount(): Account

}

sealed class UpdateAccount(
    val accountId: AccountId,
    val bankName: BankName?,
    val description: AccountDescription?
)

data class AccountSummary(
    val accountId: AccountId,
    val bankName: BankName,
    val description: AccountDescription
)

data class RemoveAccount(val accountId: AccountId)

sealed class AddNewAccountDomainError : DomainError() {

    data object AccountAlreadyExists : AddNewAccountDomainError()

}

sealed class UpdateAccountDomainError : DomainError() {

    data object AccountNotFound : UpdateAccountDomainError()
    data object InvalidRequest : UpdateAccountDomainError()

}

sealed class RemoveAccountDomainError : DomainError() {

    data object AccountNotFound : RemoveAccountDomainError()

}

