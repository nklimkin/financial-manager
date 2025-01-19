package me.nikitaklimkin.persistence.transactions.model

import arrow.core.Either
import arrow.core.raise.either
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.transaction.*
import me.nikitaklimkin.persistence.account.model.AccountPersistenceModel
import me.nikitaklimkin.persistence.account.model.toAccountId
import me.nikitaklimkin.persistence.account.model.toPersistenceId
import me.nikitaklimkin.persistence.common.model.PersistenceModel
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import java.time.OffsetDateTime
import java.util.*

data class TransactionPersistenceModel(
    @BsonId
    override val id: Id<TransactionPersistenceModel>,
    val accountId: Id<AccountPersistenceModel>,
    val name: String,
    val amount: Double,
    val type: String,
    val direction: String,
    val description: String?,
    val created: OffsetDateTime,
    val active: Boolean
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(transaction: Transaction): TransactionPersistenceModel {
            return TransactionPersistenceModel(
                transaction.id.toPersistenceId(),
                transaction.accountId.toPersistenceId(),
                transaction.name.toStringValue(),
                transaction.amount.toDoubleValue(),
                transaction.direction.name,
                transaction.type.toStringValue(),
                transaction.description,
                transaction.created,
                transaction.active
            )
        }

    }

    fun toBusiness(): Either<InvalidTransactionError, Transaction> {
        return either {
            Transaction(
                id.toTransactionId(),
                this@TransactionPersistenceModel.accountId.toAccountId().bind(),
                TransactionName.from(this@TransactionPersistenceModel.name).bind(),
                MoneyAmount.from(this@TransactionPersistenceModel.amount),
                Category.from(this@TransactionPersistenceModel.type).bind(),
                Direction.from(this@TransactionPersistenceModel.direction).bind(),
                description,
                created,
                active
            )
        }
            .mapLeft { InvalidTransactionError }
    }
}

fun TransactionId.toPersistenceId(): Id<TransactionPersistenceModel> {
    return this.toUuid().toString().toId()
}

fun Id<TransactionPersistenceModel>.toTransactionId(): TransactionId {
    return TransactionId(UUID.fromString(this.toString()))
}