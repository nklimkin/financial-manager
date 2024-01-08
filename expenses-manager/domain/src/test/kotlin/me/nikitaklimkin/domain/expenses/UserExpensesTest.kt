package me.nikitaklimkin.domain.expenses

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.nikitaklimkin.domain.*
import me.nikitaklimkin.domain.expenses.dto.ExpensesDto
import me.nikitaklimkin.domain.expenses.dto.SaveExpensesDto
import me.nikitaklimkin.domain.expenses.dto.UpdateExpensesDto
import java.time.OffsetDateTime

@ExperimentalKotest
class UserExpensesTest : BehaviorSpec({

    given("Empty expenses") {

        `when`("Build new user expenses") {

            val expenses = UserExpenses.buildNew(USER_ID)

            then("Has match properties") {

                expenses shouldNotBe null
                expenses.id shouldBe USER_ID
                expenses.getExpenses().isEmpty() shouldBe true

            }

        }

    }

    given("Valid expenses And SaveDto") {

        val expenses = UserExpenses(
            USER_ID,
            mutableListOf()
        )

        val saveDto = buildSaveExpensesDto()

        `when`("Add new") {

            val addedExpenses = expenses.addExpenses(
                ExpensesIdGeneratorFixtures(),
                saveDto
            )

            then("Has Match updated expenses and math user expenses properties") {

                addedExpenses.id shouldBe EXPENSES_ID
                expensesDtoShouldMatchSaveDto(addedExpenses, saveDto)
                expenses.getExpenses().size shouldBe 1

            }

        }

    }

    given("User Expenses with already saved data") {

        val expenses = Expenses(
            EXPENSES_ID,
            ExpensesName("bye new computer"),
            Amount(123.5),
            ExpensesType("shopping"),
            null,
            OffsetDateTime.MIN
        )
        val userExpenses = UserExpenses(
            USER_ID,
            mutableListOf(
                expenses
            )
        )

        given("Update dto with all properties") {

            val updateDto = buildUpdateExpensesDto(id = EXPENSES_ID)

            `when`("Update") {

                val result = userExpenses.updateExpenses(updateDto)

                then("Has match updated data") {

                    result.isRight() shouldBe true
                    expensesDtoShouldMatchUpdateDto(result.getOrNull()!!, updateDto)

                    val updatedUserExpenses = userExpenses.getExpenses()
                    updatedUserExpenses.size shouldBe 1
                    expensesDtoShouldMatchUpdateDto(updatedUserExpenses.first(), updateDto)

                }

            }

        }

        given("Update dto with only one properties") {

            val updateDto = buildUpdateExpensesDto(
                id = EXPENSES_ID,
                amount = null,
                type = null,
                description = "New",
                created = null
            )

            `when`("Update") {

                val result = userExpenses.updateExpenses(updateDto)

                then("Has match updated data") {

                    result.isRight() shouldBe true
                    val updated = result.getOrNull()!!
                    updated.id shouldBe updateDto.id
                    updated.name shouldBe expenses.name
                    updated.amount shouldBe expenses.amount
                    updated.type shouldBe expenses.type
                    updated.description shouldBe updateDto.description
                    updated.created shouldBe expenses.created

                    val updatedUserExpenses = userExpenses.getExpenses()
                    updatedUserExpenses.size shouldBe 1
                    val userExpensesUpdatedElement = updatedUserExpenses.first()
                    userExpensesUpdatedElement.id shouldBe expenses.id
                    userExpensesUpdatedElement.name shouldBe expenses.name
                    userExpensesUpdatedElement.amount shouldBe expenses.amount
                    userExpensesUpdatedElement.type shouldBe expenses.type
                    userExpensesUpdatedElement.description shouldBe updateDto.description
                    userExpensesUpdatedElement.created shouldBe expenses.created

                }

            }

        }

        given("Update dto with not match id") {

            val updateDto = buildUpdateExpensesDto()

            `when`("Update") {

                val result = userExpenses.updateExpenses(updateDto)

                then("Has Error Result") {

                    result.isLeft() shouldBe true
                    result.leftOrNull()!! shouldBe ExpensesNotFoundError

                }
            }

        }

        given("Delete dto with match id") {

            val deleteId = EXPENSES_ID

            `when`("Delete expenses") {

                val result = userExpenses.deleteExpenses(deleteId)

                then("Delete from expenses list and has match result") {

                    result.isRight() shouldBe true
                    val deleted = result.getOrNull()!!
                    deleted shouldBe expenses.toDto()

                }

            }

        }

        given("Delete dto with not match id") {

            val deleteId = buildExpensesId()

            `when`("Delete expenses") {

                val result = userExpenses.deleteExpenses(deleteId)

                then("Has Error Result") {

                    result.isLeft() shouldBe true
                    result.leftOrNull()!! shouldBe ExpensesNotFoundError

                }

            }

        }

    }

})

fun expensesDtoShouldMatchSaveDto(expensesDto: ExpensesDto, saveDto: SaveExpensesDto) {
    expensesDto.name shouldBe saveDto.name
    expensesDto.amount shouldBe saveDto.amount
    expensesDto.type shouldBe saveDto.type
    expensesDto.description shouldBe saveDto.description
    expensesDto.created shouldBe saveDto.created
}

fun expensesDtoShouldMatchUpdateDto(expensesDto: ExpensesDto, updateDto: UpdateExpensesDto) {
    expensesDto.id shouldBe updateDto.id
    expensesDto.name shouldBe updateDto.name
    expensesDto.amount shouldBe updateDto.amount
    expensesDto.type shouldBe updateDto.type
    expensesDto.description shouldBe updateDto.description
    expensesDto.created shouldBe updateDto.created
}