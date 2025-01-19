package me.nikitaklimkin.domain.user

import arrow.core.getOrElse
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import me.nikitaklimkin.domain.INVALID_USER_NAME
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.USER_NAME
import java.time.OffsetDateTime

@ExperimentalKotest
class UserTest : BehaviorSpec({

    given("invalid create user name params") {

        `when`("create user name") {

            val result = UserName.from(INVALID_USER_NAME)

            then("has error") {

                result.isLeft() shouldBe true
                result.leftOrNull()!!.shouldBeInstanceOf<CreateUserError.UserNameEmpty>()
            }

        }

    }

    given("valid user property") {

        given("new user property") {

            `when`("execute create method without Tb info") {

                val user = User.buildNew(USER_ID, USER_NAME)

                then("Has match property") {

                    user.isRight() shouldBe true

                    val userValue = user.getOrElse { null }!!

                    userValue.id shouldBe USER_ID
                    userValue.active() shouldBe true
                    userValue.userName() shouldBe USER_NAME
                }

            }
        }

        given("inactive user to make it active") {

            val user = User(USER_ID, USER_NAME, false, OffsetDateTime.MIN)

            `when`("make active") {

                user.makeActive()

                then("has active flag") {

                    user.active() shouldBe true
                }

            }

        }

        given("active user to make deactivate it") {

            val user = User(USER_ID, USER_NAME, true, OffsetDateTime.MIN)

            `when`("deactivate user") {

                user.deactivate()

                then("has false active flag") {

                    user.active() shouldBe false
                }

            }

        }

    }

})
