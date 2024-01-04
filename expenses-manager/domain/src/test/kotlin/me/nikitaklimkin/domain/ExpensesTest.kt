package me.nikitaklimkin.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.*

class ExpensesTest : BehaviorSpec({

    given("Not valid input to create amount") {

        val invalidAmount = -10.0

        `when`("Create amount") {

            val result = Amount.from(invalidAmount)

            then("Has Error") {

                result.isLeft() shouldBe true
                result.leftOrNull().shouldBeInstanceOf<NotValidPriceError>()

            }

        }

    }

    given("Valid input for amount") {

        val validAmount = 10.0

        `when`("Create amount") {

            val result = Amount.from(validAmount)

            then("Has Success result") {

                result.isRight() shouldBe true
                result.getOrNull()?.toDoubleValue() shouldBe validAmount

            }

        }

    }

    given("Valid input to create expenses") {

        val expensesId = ExpensesId(UUID.randomUUID())
        val amount = Amount(5.0)
        val userId = UserId(UUID.randomUUID())
        val description = "test-expense"

        and("Invalid type") {

            val invalidType = ""

            `when`("Create expenses") {

                val result = Expenses.buildNew(expensesId, amount, invalidType, description, userId)

                then("Has Error") {

                    result.isLeft() shouldBe true
                    result.leftOrNull().shouldBeInstanceOf<CreateExpensesError.EmptyTypeError>()

                }

            }

        }

        and("Valid type") {

            val validType = "shopping"

            `when`("Create expenses") {

                val result = Expenses.buildNew(expensesId, amount, validType, description, userId)

                then("Has Success") {

                    result.isRight() shouldBe true
                    val resultValue = result.getOrNull()!!
                    resultValue.id shouldBe expensesId
                    resultValue.amount shouldBe amount
                    resultValue.description shouldBe description
                    resultValue.type shouldBe validType
                    resultValue.userId shouldBe userId

                }

            }

        }

    }


})
