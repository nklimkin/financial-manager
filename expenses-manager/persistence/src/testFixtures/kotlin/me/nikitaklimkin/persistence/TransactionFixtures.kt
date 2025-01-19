package me.nikitaklimkin.persistence

import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.transaction.Direction
import me.nikitaklimkin.persistence.account.model.AccountPersistenceModel
import me.nikitaklimkin.persistence.account.model.toPersistenceId
import me.nikitaklimkin.persistence.transactions.model.TransactionPersistenceModel
import me.nikitaklimkin.persistence.transactions.model.toPersistenceId
import org.litote.kmongo.Id
import java.math.BigDecimal
import java.time.OffsetDateTime

fun buildTransactionPersistenceModel(
    id: Id<TransactionPersistenceModel> = TRANSACTION_ID.toPersistenceId(),
    accountId: Id<AccountPersistenceModel> = ACCOUNT_ID.toPersistenceId(),
    name: String = VALID_NAME,
    amount: BigDecimal = VALID_AMOUNT,
    type: String = VALID_TYPE,
    direction: String = Direction.IN.name,
    description: String? = DESCRIPTION,
    created: OffsetDateTime = OffsetDateTime.now()
) = TransactionPersistenceModel(
    id,
    accountId,
    name,
    amount,
    type,
    direction,
    description,
    created,
    true
)