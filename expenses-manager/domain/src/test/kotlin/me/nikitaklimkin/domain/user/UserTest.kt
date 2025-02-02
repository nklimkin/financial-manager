package me.nikitaklimkin.domain.user

import arrow.core.getOrElse
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import me.nikitaklimkin.domain.*
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

    given("invalid params to create oauthid") {

        `when`("crete oauth id") {

            val result = OauthId.from("")

            then("Has error") {

                result.isLeft() shouldBe true
                result.leftOrNull()!!.shouldBeInstanceOf<CreateOauthIdError>()
            }

        }

    }

    given("valid user property") {

        given("new user property") {

            `when`("execute create method without Tb info") {

                val user = User.build(FixturesUserIdGenerator(), USER_OAUTH_ID, USER_NAME)

                then("Has match property") {

                    user.isRight() shouldBe true

                    val userValue = user.getOrElse { null }!!

                    userValue.id shouldBe USER_ID
                    userValue.active() shouldBe true
                    userValue.userName() shouldBe USER_NAME
                    userValue.oauthId shouldBe USER_OAUTH_ID
                }

            }
        }

        given("inactive user to make it active") {

            val user = User(USER_ID, USER_OAUTH_ID, USER_NAME, false, OffsetDateTime.MIN)

            `when`("make active") {

                user.makeActive()

                then("has active flag") {

                    user.active() shouldBe true
                }

            }

        }

        given("active user to make deactivate it") {

            val user = User(USER_ID, USER_OAUTH_ID, USER_NAME, true, OffsetDateTime.MIN)

            `when`("deactivate user") {

                user.deactivate()

                then("has false active flag") {

                    user.active() shouldBe false
                }

            }

        }

    }

})
