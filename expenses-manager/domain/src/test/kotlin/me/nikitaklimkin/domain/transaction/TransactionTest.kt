package me.nikitaklimkin.domain.transaction

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.nikitaklimkin.domain.*
import java.time.OffsetDateTime

@ExperimentalKotest
class TransactionTest : BehaviorSpec({

    given("Valid input to create transaction category") {

        `when`("Create category") {

            val category = Category.from(VALID_TYPE)

            then("Has Success result") {

                category.isRight() shouldBe true
                category.getOrNull() shouldNotBe null
                category.getOrNull()?.toStringValue()!! shouldBe VALID_TYPE

            }

        }

    }

    given("Invalid input to create category type") {

        `when`("Create category type") {

            val type = Category.from(INVALID_TYPE)

            then("Has Error result") {

                type.isLeft() shouldBe true
                type.leftOrNull() shouldNotBe null
                type.leftOrNull()!! shouldBe NotValidCategory

            }

        }

    }

    given("Valid input to create transaction name") {

        `when`("Create transaction name") {

            val name = TransactionName.from(VALID_NAME)

            then("Has Success Result") {

                name.isRight() shouldBe true
                name.getOrNull()!!.toStringValue() shouldBe VALID_NAME

            }

        }

    }

    given("Invalid input to create transaction name") {

        `when`("Create transaction name") {

            val name = TransactionName.from(INVALID_NAME)

            then("Has Error Result") {

                name.isLeft() shouldBe true
                name.leftOrNull()!! shouldBe NotValidTransactionNameError

            }

        }

    }

    given("Valid input to create transaction direction") {
        `when`("Create transaction direction") {

            val direction = Direction.from(VALID_DIRECTION)

            then("Has success result") {

                direction.isRight() shouldBe true
                direction.getOrNull()!! shouldBe Direction.IN

            }

        }

    }

    given("Invalid input to create transaction direction") {
        `when`("Create transaction direction") {

            val direction = Direction.from(INVALID_DIRECTION)

            then("Has error result") {

                direction.isLeft() shouldBe true
                direction.leftOrNull()!! shouldBe InvalidDirectionError

            }

        }

    }

    given("Valid input to create transaction") {

        val name = buildName()
        val amount = buildAmount()
        val type = buildType()

        `when`("Create transaction") {

            val result = Transaction.build(
                TransactionIdGeneratorFixtures(),
                ACCOUNT_ID,
                name,
                amount,
                type,
                Direction.IN,
                DESCRIPTION
            )

            then("Has Success") {

                result.id shouldBe TRANSACTION_ID
                result.accountId shouldBe ACCOUNT_ID
                result.name shouldBe name
                result.amount shouldBe amount
                result.description shouldBe DESCRIPTION
                result.type shouldBe type
                result.direction shouldBe Direction.IN
                result.active shouldBe true
            }

        }

    }

    given("Valid transaction to map to DTO") {

        val transaction = buildTransaction()

        `when`("Map to DTO") {

            val dto = transaction.summary()

            then("Has match result") {

                dto shouldNotBe null
                dto.id shouldBe transaction.id
                dto.name shouldBe transaction.name
                dto.amount shouldBe transaction.amount
                dto.type shouldBe transaction.type
                dto.direction shouldBe transaction.direction
                dto.description shouldBe transaction.description
                dto.created shouldBe transaction.created

            }

        }

    }

    given("Request to update transaction") {

        val transaction = buildTransaction(created = OffsetDateTime.MAX)

        `when`("Update by empty request") {

            transaction.update(buildEmptyUpdateTransaction())

            then("Source not changed") {

                transaction.name shouldBe buildName()
                transaction.amount shouldBe buildAmount()
                transaction.type shouldBe buildType()
                transaction.direction shouldBe Direction.IN
                transaction.description shouldBe DESCRIPTION
                transaction.created shouldBe OffsetDateTime.MAX

            }
        }

        `when`("Update by not empty request") {

            val newName = TransactionName.from("Upd-test-name").getOrNull()!!
            val newAmount = MoneyAmount.from(545.12)
            val newType = Category.from("Upd-test-category").getOrNull()!!
            val newDirection = Direction.OUT
            val newDescription = "Upd-test-description"
            val newCreated = OffsetDateTime.MIN

            transaction.update(
                buildUpdateTransaction(
                    newName,
                    newAmount,
                    newType,
                    newDirection,
                    newDescription,
                    newCreated
                )
            )

            then("Has match result")

            transaction.name shouldBe newName
            transaction.amount shouldBe newAmount
            transaction.type shouldBe newType
            transaction.direction shouldBe newDirection
            transaction.description shouldBe newDescription
            transaction.created shouldBe newCreated

        }

    }

    given("Transaction to deactivate") {

        val transaction = buildTransaction()

        `when`("Deactivate") {

            transaction.deactivate()

            then("Has match result") {

                transaction.active shouldBe false

            }
        }

    }
})
