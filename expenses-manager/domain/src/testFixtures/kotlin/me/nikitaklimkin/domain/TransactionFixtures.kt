package me.nikitaklimkin.domain

import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.*
import me.nikitaklimkin.domain.transaction.dto.TransactionDTO
import me.nikitaklimkin.domain.transaction.dto.UpdateTransaction
import java.time.OffsetDateTime
import java.util.*

const val INVALID_AMOUNT = -10.0
const val VALID_AMOUNT = 10.0
const val VALID_TYPE = "home"
const val INVALID_TYPE = ""
const val DESCRIPTION = "test-expense"
const val VALID_NAME = "swimming"
const val INVALID_NAME = ""
const val VALID_DIRECTION = "IN"
const val INVALID_DIRECTION = "LEFT"
val TRANSACTION_ID = TransactionId(UUID.randomUUID())
val TRANSACTION_ID_2 = TransactionId(UUID.randomUUID())
val TRANSACTION_ID_3 = TransactionId(UUID.randomUUID())

fun buildTransactionId() = TransactionId(UUID.randomUUID())

fun buildAmount() = MoneyAmount.from(VALID_AMOUNT)

fun buildType() = Category.from(VALID_TYPE).getOrNull()!!

fun buildName() = TransactionName.from(VALID_NAME).getOrNull()!!

fun buildUpdateTransactionDto(
    name: TransactionName = buildName(),
    amount: MoneyAmount? = buildAmount(),
    type: Category? = buildType(),
    direction: Direction? = Direction.IN,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now()
) = UpdateTransaction(
    name,
    amount,
    type,
    direction,
    description,
    created
)

fun buildTransactionDto() = TransactionDTO(
    buildTransactionId(),
    buildAccountId(),
    buildName(),
    buildAmount(),
    buildType(),
    Direction.IN,
    DESCRIPTION,
    OffsetDateTime.now()
)

fun buildTransaction(
    id: TransactionId = TRANSACTION_ID,
    accountId: AccountId = ACCOUNT_ID,
    name: TransactionName = buildName(),
    amount: MoneyAmount = buildAmount(),
    type: Category = buildType(),
    direction: Direction = Direction.IN,
    description: String? = DESCRIPTION,
    created: OffsetDateTime = OffsetDateTime.now()
) = Transaction(id, accountId, name, amount, type, direction, description, created, true)

fun buildEmptyUpdateTransaction() = UpdateTransaction(null, null, null, null, null, null)
fun buildUpdateTransaction(
    name: TransactionName? = buildName(),
    amount: MoneyAmount? = buildAmount(),
    type: Category? = buildType(),
    direction: Direction? = Direction.IN,
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now()
) = UpdateTransaction(name, amount, type, direction, description, created)

class TransactionIdGeneratorFixtures : TransactionIdGenerator {

    override fun generate(): TransactionId {
        return TRANSACTION_ID
    }
}

class RandomTransactionIdGeneratorFixtures : TransactionIdGenerator {

    override fun generate(): TransactionId {
        return TransactionId(UUID.randomUUID())
    }

}