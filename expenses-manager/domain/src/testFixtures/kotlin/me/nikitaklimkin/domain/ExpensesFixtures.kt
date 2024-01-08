package me.nikitaklimkin.domain

import me.nikitaklimkin.domain.expenses.*
import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.domain.expenses.dto.SaveExpensesDto
import me.nikitaklimkin.domain.expenses.dto.UpdateExpensesDto
import java.time.OffsetDateTime
import java.util.*

const val INVALID_AMOUNT = -10.0
const val VALID_AMOUNT = 10.0
const val VALID_TYPE = "home"
const val INVALID_TYPE = ""
const val DESCRIPTION = "test-expense"
const val VALID_NAME = "swimming"
const val INVALID_NAME = ""
val EXPENSES_ID = ExpensesId(UUID.randomUUID())

fun buildExpensesId() = ExpensesId(UUID.randomUUID())

fun buildAmount() = Amount.from(VALID_AMOUNT).getOrNull()!!

fun buildType() = ExpensesType.from(VALID_TYPE).getOrNull()!!

fun buildName() = ExpensesName.from(VALID_NAME).getOrNull()!!

fun buildSaveExpensesDto() = SaveExpensesDto(
    buildName(),
    buildAmount(),
    buildType(),
    DESCRIPTION,
    OffsetDateTime.now()
)

fun buildUpdateExpensesDto(
    id: ExpensesId = buildExpensesId(),
    name: ExpensesName = buildName(),
    amount: Amount? = buildAmount(),
    type: ExpensesType? = buildType(),
    description: String? = DESCRIPTION,
    created: OffsetDateTime? = OffsetDateTime.now()
) = UpdateExpensesDto(
    id,
    name,
    amount,
    type,
    description,
    created
)

fun buildExpensesDto() = ExpensesDto(
    buildExpensesId(),
    buildName(),
    buildAmount(),
    buildType(),
    DESCRIPTION,
    OffsetDateTime.now()
)

class ExpensesIdGeneratorFixtures : ExpensesIdGenerator {

    override fun generate(): ExpensesId {
        return EXPENSES_ID
    }
}