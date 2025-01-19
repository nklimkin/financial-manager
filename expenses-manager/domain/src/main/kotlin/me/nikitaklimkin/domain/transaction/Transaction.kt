package me.nikitaklimkin.domain.transaction

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.dto.TransactionDTO
import me.nikitaklimkin.domain.transaction.dto.UpdateTransaction
import me.nikitaklimkin.model.AggregateRoot
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject
import java.time.OffsetDateTime
import java.util.*

data class TransactionId(private val value: UUID) : ValueObject {

    companion object {

        fun from(value: String): Either<InvalidTransactionIdError, TransactionId> {
            return try {
                TransactionId(UUID.fromString(value)).right()
            } catch (exception: IllegalArgumentException) {
                return InvalidTransactionIdError.left()
            }
        }

    }

    fun toUuid() = value
}

object InvalidTransactionIdError : DomainError()

class Transaction(
    val id: TransactionId,
    val accountId: AccountId,
    var name: TransactionName,
    var amount: MoneyAmount,
    var type: Category,
    var direction: Direction,
    var description: String?,
    var created: OffsetDateTime,
    var active: Boolean
) : AggregateRoot<TransactionId>(id) {

    fun deactivate() {
        active = false
    }

    fun summary(): TransactionDTO {
        return TransactionDTO(
            id,
            accountId,
            name,
            amount,
            type,
            direction,
            description,
            created
        )
    }

    fun update(updateInfo: UpdateTransaction) {
        updateInfo.name?.let { this.name = it }
        updateInfo.amount?.let { this.amount = it }
        updateInfo.type?.let { this.type = it }
        updateInfo.direction?.let { this.direction = it }
        updateInfo.description?.let { this.description = it }
        updateInfo.created?.let { this.created = it }
    }

    companion object {

        fun build(
            idGenerator: TransactionIdGenerator,
            accountId: AccountId,
            name: TransactionName,
            amount: MoneyAmount,
            type: Category,
            direction: Direction,
            description: String?
        ): Transaction {
            return Transaction(
                idGenerator.generate(),
                accountId,
                name,
                amount,
                type,
                direction,
                description,
                OffsetDateTime.now(),
                true
            )
        }

    }
}

enum class Direction {

    IN,
    OUT;

    companion object {
        fun from(value: String): Either<InvalidDirectionError, Direction> {
            return try {
                Direction.valueOf(value).right()
            } catch (exception: IllegalArgumentException) {
                InvalidDirectionError.left()
            }
        }
    }

}

data object InvalidTransactionError : DomainError()

data object InvalidDirectionError : DomainError()