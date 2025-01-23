package me.nikitaklimkin.domain.account

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.nikitaklimkin.domain.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@ExperimentalKotest
class AccountTest : BehaviorSpec({

    given("Create Account Id") {

        `when`("Create invalid id") {

            val accountId = AccountId.from("")

            then("Has error result") {

                accountId.isLeft() shouldBe true
                accountId.leftOrNull()!! shouldBe CreateAccountIdError

            }

        }

        `when`("Create valid id") {

            val source = UUID.randomUUID()
            val accountId = AccountId.from(source.toString())

            then("Has match result") {

                accountId.isRight() shouldBe true
                accountId.getOrNull()!!.toUuid() shouldBe source

            }

        }

    }

    given("Create account request") {

        `when`("Create broker account") {

            val account = newBrokerAccount().buildAccount(FixturesAccountIdGenerator())

            then("Has match result") {

                account.id shouldNotBe null
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.active shouldBe true
                account.userId shouldBe USER_ID

            }

        }

        `when`("Create card account") {

            val account = newCardAccount().buildAccount(FixturesAccountIdGenerator())

            then("Has match result") {

                account.id shouldNotBe null
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.active shouldBe true
                account.userId shouldBe USER_ID
                account.balance() shouldBe TEST_MONEY_AMOUNT

            }

        }

        `when`("Create deposit account") {

            val account = newDepositAccount().buildAccount(FixturesAccountIdGenerator())

            then("Has match result") {

                account.id shouldNotBe null
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.active shouldBe true
                account.userId shouldBe USER_ID
                account.initialBalance shouldBe TEST_MONEY_AMOUNT
                account.expectedFinalBalance shouldBe TEST_MONEY_AMOUNT_2
                account.openedDate shouldBe OffsetDateTime.MIN
                account.closedDate shouldBe OffsetDateTime.MAX
                account.interest shouldBe TEST_INTEREST

            }

        }

        `when`("Create piggy account") {

            val account = newPiggyAccount().buildAccount(FixturesAccountIdGenerator())

            then("Has match result") {

                account.id shouldNotBe null
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.active shouldBe true
                account.userId shouldBe USER_ID
                account.balance() shouldBe TEST_MONEY_AMOUNT
                account.interest shouldBe TEST_INTEREST

            }

        }

    }

    given("Broker account") {

        val account = brokerAccount()

        `when`("Deactivate") {

            account.deactivate()

            then("Has match result") {

                account.active shouldBe false

            }

        }

        `when`("Get summary") {

            val summary = account.summary()

            then("Has match result") {

                summary.accountId shouldBe account.id
                summary.bankName shouldBe account.bankName
                summary.description shouldBe account.description

            }

        }

        `when`("Update account with valid not full request") {

            val result = account.updateInfo(updateBrokerAccount(bankName = TEST_BANK_NAME_2, null))

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME_2
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.balance() shouldBe TEST_MONEY_AMOUNT

            }

        }

        `when`("Update account with valid full request") {

            val result = account.updateInfo(
                updateBrokerAccount(
                    bankName = TEST_BANK_NAME,
                    description = TEST_ACCOUNT_DESCRIPTION_2
                )
            )

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION_2
                account.balance() shouldBe TEST_MONEY_AMOUNT

            }

        }

        `when`("Update account with invalid request") {

            val result = account.updateInfo(updateDepositAccount())

            then("Has match result") {

                result.isRight() shouldBe false
                result.leftOrNull()!! shouldBe UpdateAccountDomainError.InvalidRequest
            }

        }

    }

    given("Card account") {

        val account = cardAccount()

        `when`("Deactivate") {

            account.deactivate()

            then("Has match result") {

                account.active shouldBe false

            }

        }

        `when`("Get summary") {

            val summary = account.summary()

            then("Has match result") {

                summary.accountId shouldBe account.id
                summary.bankName shouldBe account.bankName
                summary.description shouldBe account.description

            }

        }

        `when`("Update account with valid not full request") {

            val result = account.updateInfo(updateCardAccount(bankName = TEST_BANK_NAME_2, null))

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME_2
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.balance() shouldBe TEST_MONEY_AMOUNT

            }

        }

        `when`("Update account with valid full request") {

            val result = account.updateInfo(
                updateCardAccount(
                    bankName = TEST_BANK_NAME,
                    description = TEST_ACCOUNT_DESCRIPTION_2
                )
            )

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION_2
                account.balance() shouldBe TEST_MONEY_AMOUNT

            }

        }

        `when`("Update account with invalid request") {

            val result = account.updateInfo(updateDepositAccount())

            then("Has match result") {

                result.isRight() shouldBe false
                result.leftOrNull()!! shouldBe UpdateAccountDomainError.InvalidRequest
            }

        }

    }

    given("Deposit account") {

        val account = depositAccount()

        `when`("Deactivate") {

            account.deactivate()

            then("Has match result") {

                account.active shouldBe false

            }

        }

        `when`("Get summary") {

            val summary = account.summary()

            then("Has match result") {

                summary.accountId shouldBe account.id
                summary.bankName shouldBe account.bankName
                summary.description shouldBe account.description

            }

        }

        `when`("Update account with valid not full request") {

            val result = account.updateInfo(updateDepositAccount(bankName = TEST_BANK_NAME_2, null))

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME_2
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.interest shouldBe TEST_INTEREST
                account.initialBalance shouldBe TEST_MONEY_AMOUNT
                account.expectedFinalBalance shouldBe TEST_MONEY_AMOUNT_2
                account.openedDate shouldBe OffsetDateTime.MIN
                account.closedDate shouldBe OffsetDateTime.MAX

            }

        }

        `when`("Update account with valid full request") {

            val openDate = OffsetDateTime.of(2024, 12, 12, 10, 10, 10, 10, ZoneOffset.UTC)
            val closedDate = OffsetDateTime.of(2025, 12, 12, 10, 10, 10, 10, ZoneOffset.UTC)

            val result = account.updateInfo(
                updateDepositAccount(
                    bankName = TEST_BANK_NAME,
                    description = TEST_ACCOUNT_DESCRIPTION_2,
                    interest = TEST_INTEREST_2,
                    openDate = openDate,
                    closedDate = closedDate,
                )
            )

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION_2
                account.interest shouldBe TEST_INTEREST_2
                account.openedDate shouldBe openDate
                account.closedDate shouldBe closedDate
                account.initialBalance shouldBe TEST_MONEY_AMOUNT
                account.expectedFinalBalance shouldBe TEST_MONEY_AMOUNT_2

            }

        }

        `when`("Update account with invalid request") {

            val result = account.updateInfo(updateCardAccount())

            then("Has match result") {

                result.isRight() shouldBe false
                result.leftOrNull()!! shouldBe UpdateAccountDomainError.InvalidRequest
            }

        }

    }

    given("Piggy account") {

        val account = piggyAccount()

        `when`("Deactivate") {

            account.deactivate()

            then("Has match result") {

                account.active shouldBe false

            }

        }

        `when`("Get summary") {

            val summary = account.summary()

            then("Has match result") {

                summary.accountId shouldBe account.id
                summary.bankName shouldBe account.bankName
                summary.description shouldBe account.description

            }

        }

        `when`("Update account with valid not full request") {

            val result = account.updateInfo(updatePiggyAccount(bankName = TEST_BANK_NAME_2, null))

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME_2
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION
                account.interest shouldBe TEST_INTEREST
                account.balance() shouldBe TEST_MONEY_AMOUNT

            }

        }

        `when`("Update account with valid full request") {

            val result = account.updateInfo(
                updatePiggyAccount(
                    bankName = TEST_BANK_NAME,
                    description = TEST_ACCOUNT_DESCRIPTION_2,
                    interest = TEST_INTEREST_2
                )
            )

            then("Has match result") {

                result.isRight() shouldBe true
                account.id shouldBe ACCOUNT_ID
                account.bankName shouldBe TEST_BANK_NAME
                account.description shouldBe TEST_ACCOUNT_DESCRIPTION_2
                account.interest shouldBe TEST_INTEREST_2
                account.balance() shouldBe TEST_MONEY_AMOUNT

            }

        }

        `when`("Update account with invalid request") {

            val result = account.updateInfo(updateDepositAccount())

            then("Has match result") {

                result.isRight() shouldBe false
                result.leftOrNull()!! shouldBe UpdateAccountDomainError.InvalidRequest
            }

        }

    }

    given("Generic account") {

        val account = depositAccount()

        `when`("Can be process by valid user") {

            val result = account.canBeProcessByUser(USER_ID)

            then("Has match result") {

                result shouldBe true

            }

        }

        `when`("Can be process by invalid user") {

            val result = account.canBeProcessByUser(USER_ID_2)

            then("Has match result") {

                result shouldBe false

            }

        }

    }

})