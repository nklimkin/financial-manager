package me.nikitaklimkin.domain.user

import arrow.core.getOrElse
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import me.nikitaklimkin.domain.*

@ExperimentalKotest
class UserTest : BehaviorSpec({

    given("invalid create user name params") {

        `when`("create user name") {

            val result = UserName.create(INVALID_USER_NAME)

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
                    userValue.telegramUser() shouldBe null
                }

            }


            `when`("execute create with tb info") {

                val user = User.buildNewByTelegram(
                    USER_ID,
                    TELEGRAM_CHAT_ID,
                    TB_USER_NAME
                )

                then("Has math property") {

                    user.isRight() shouldBe true
                    val userValue = user.getOrElse { null }!!

                    userValue.id shouldBe USER_ID
                    userValue.userName() shouldBe null
                    userValue.active() shouldBe true
                    userValue.telegramUser() shouldNotBe null
                    userValue.telegramUser()!!.chatId shouldBe TELEGRAM_CHAT_ID
                    userValue.telegramUser()!!.userName shouldBe TB_USER_NAME

                }

            }
        }

        given("User to add new  telegram info") {

            val user = User(USER_ID, USER_NAME, null, true)


            `when`("Update by telegram info") {

                val updatedInfo = user.updateByTelegramInfo(TELEGRAM_CHAT_ID, TB_USER_NAME)

                then("Has updated tb info") {

                    updatedInfo.isRight() shouldBe true
                    user.telegramUser() shouldNotBe null
                    user.telegramUser()?.userName shouldBe TB_USER_NAME
                    user.telegramUser()?.chatId shouldBe TELEGRAM_CHAT_ID

                }

            }

        }

        given("User with telegramInfo to updateBy new telegramInfo") {

            val telegramUser = TelegramUser(1L, TB_USER_NAME)

            val user = User(USER_ID, USER_NAME, telegramUser, true)

            `when`("update by telegram info") {

                val updatedInfo = user.updateByTelegramInfo(2L, UserName("upd-tb-user-name"))

                then("Has Error") {

                    user.telegramUser() shouldBe telegramUser
                    updatedInfo.isLeft() shouldBe true
                    updatedInfo.leftOrNull()!!.shouldBeInstanceOf<CreateUserError.TelegramInfoAlreadyExists>()

                }

            }

        }

        given("User user by new user name") {

            val user = User(USER_ID, null, null, true)

            val newUserName = UserName("upd-user-name")

            `when`("update by new userName") {

                val updatedInfo = user.updateByUserName(newUserName)

                then("user has new name") {

                    updatedInfo.isRight() shouldBe true
                    user.userName() shouldBe newUserName

                }

            }

        }

        given("update user with already existed user name by new") {

            val user = User(USER_ID, USER_NAME, null, true)

            `when`("update by new user name") {

                val updatedInfo = user.updateByUserName(UserName("upd-user-name"))

                then("has error") {

                    user.userName() shouldBe USER_NAME
                    updatedInfo.isLeft() shouldBe true
                    updatedInfo.leftOrNull()!!.shouldBeInstanceOf<CreateUserError.UserNameInfoAlreadyExists>()
                }

            }

        }

        given("inactive user to make it active") {

            val user = User(USER_ID, null, null, false)

            `when`("make active") {

                user.makeActive()

                then("has active flag") {

                    user.active() shouldBe true
                }

            }

        }

        given("active user to make deactivate it") {

            val user = User(USER_ID, null, null, true)

            `when`("deactivate user") {

                user.deactivate()

                then("has false active flag") {

                    user.active() shouldBe false
                }

            }

        }

    }

})
