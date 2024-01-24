package me.nikitaklimkin.domain.expenses

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.expenses.*
import java.time.OffsetDateTime
import java.util.*

@ExperimentalKotest
class ExpensesTest : BehaviorSpec({

    given("Not valid input to create amount") {

        `when`("Create amount") {

            val result = Amount.from(INVALID_AMOUNT)

            then("Has Error") {

                result.isLeft() shouldBe true
                result.leftOrNull().shouldBeInstanceOf<NotValidPriceError>()

            }

        }

    }

    given("Valid input for amount") {

        `when`("Create amount") {

            val result = Amount.from(VALID_AMOUNT)

            then("Has Success result") {

                result.isRight() shouldBe true
                result.getOrNull()?.toDoubleValue() shouldBe VALID_AMOUNT

            }

        }

    }

    given("Valid input to create expenses type") {

        `when`("Create expenses type") {

            val type = ExpensesType.from(VALID_TYPE)

            then("Has Success result") {

                type.isRight() shouldBe true
                type.getOrNull() shouldNotBe null
                type.getOrNull()?.toStringValue()!! shouldBe VALID_TYPE

            }

        }

    }

    given("Invalid input to create expenses type") {

        `when`("Create expenses type") {

            val type = ExpensesType.from(INVALID_TYPE)

            then("Has Error result") {

                type.isLeft() shouldBe true
                type.leftOrNull() shouldNotBe null
                type.leftOrNull()!! shouldBe NotValidExpensesType

            }

        }

    }

    given("Valid input to create expenses name") {

        `when`("Create expenses name") {

            val name = ExpensesName.from(VALID_NAME)

            then("Has Success Result") {

                name.isRight() shouldBe true
                name.getOrNull()!!.toStringValue() shouldBe VALID_NAME

            }

        }

    }

    given("Invalid input to create expenses name") {

        `when`("Create expenses name") {

            val name = ExpensesName.from(INVALID_NAME)

            then("Has Error Result") {

                name.isLeft() shouldBe true
                name.leftOrNull()!! shouldBe NotValidExpensesNameError

            }

        }

    }

    given("Valid input to create expenses") {

        val expensesId = buildExpensesId()
        val name = buildName()
        val amount = buildAmount()
        val type = buildType()

        `when`("Create expenses") {

            val result = Expenses(
                expensesId,
                name,
                amount,
                type,
                DESCRIPTION,
                OffsetDateTime.now()
            )

            then("Has Success") {

                result.id shouldBe expensesId
                result.name shouldBe name
                result.amount shouldBe amount
                result.description shouldBe DESCRIPTION
                result.type shouldBe type
            }

        }

    }

    given("Valid expenses to map to DTO") {

        val expenses = Expenses(
            buildExpensesId(),
            buildName(),
            buildAmount(),
            buildType(),
            DESCRIPTION,
            OffsetDateTime.now()
        )

        `when`("Map to DTO") {

            val dto = expenses.toDto()

            then("Has match result") {

                dto shouldNotBe null
                dto.id shouldBe expenses.id
                dto.name shouldBe expenses.name
                dto.amount shouldBe expenses.amount
                dto.type shouldBe expenses.type
                dto.description shouldBe expenses.description
                dto.created shouldBe expenses.created

            }

        }

    }
})
