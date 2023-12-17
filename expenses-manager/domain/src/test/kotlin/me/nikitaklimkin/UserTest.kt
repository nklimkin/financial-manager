package me.nikitaklimkin

import arrow.core.getOrElse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.*

class UserTest : BehaviorSpec({

    given("invalid create user name params") {

        val invalidUserName = ""

        `when`("create user name") {

            val result = UserName.create(invalidUserName)

            then("has error") {

                result.isLeft() shouldBe true
                result.leftOrNull()!!.shouldBeInstanceOf<CreateUserError.UserNameEmpty>()
            }

        }

    }

    given("valid user property") {

        val id = UserId(UUID.randomUUID())
        val userName = UserName("test")

        val chatId = 1L
        val tbUserName = UserName("tb-user-name")

        given("new user property") {

            `when`("execute create method without Tb info") {

                val user = User.buildNew(id, userName)

                then("Has match property") {

                    user.isRight() shouldBe true

                    val userValue = user.getOrElse { null }!!

                    userValue.id shouldBe id
                    userValue.active() shouldBe true
                    userValue.userName() shouldBe userName
                    userValue.telegramUser() shouldBe null
                }

            }


            `when`("execute create with tb info") {

                val user = User.buildNewByTelegram(
                    id,
                    chatId,
                    tbUserName
                )

                then("Has math property") {

                    user.isRight() shouldBe true
                    val userValue = user.getOrElse { null }!!

                    userValue.id shouldBe id
                    userValue.userName() shouldBe null
                    userValue.active() shouldBe true
                    userValue.telegramUser() shouldNotBe null
                    userValue.telegramUser()!!.chatId shouldBe chatId
                    userValue.telegramUser()!!.userName shouldBe tbUserName

                }

            }
        }

        given("User to add new  telegram info") {

            val user = User(id, userName, null, true)


            `when`("Update by telegram info") {

                val updatedInfo = user.updateByTelegramInfo(chatId, tbUserName)

                then("Has updated tb info") {

                    updatedInfo.isRight() shouldBe true
                    user.telegramUser() shouldNotBe null
                    user.telegramUser()?.userName shouldBe tbUserName
                    user.telegramUser()?.chatId shouldBe chatId

                }

            }

        }

        given("User with telegramInfo to updateBy new telegramInfo") {

            val telegramUser = TelegramUser(1L, tbUserName)

            val user = User(id, userName, telegramUser, true)

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

            val user = User(id, null, null, true)

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

            val user = User(id, userName, null, true)

            `when`("update by new user name") {

                val updatedInfo = user.updateByUserName(UserName("upd-user-name"))

                then("has error") {

                    user.userName() shouldBe userName
                    updatedInfo.isLeft() shouldBe true
                    updatedInfo.leftOrNull()!!.shouldBeInstanceOf<CreateUserError.UserNameInfoAlreadyExists>()
                }

            }

        }

        given("inactive user to make it active") {

            val user = User(id, null, null, false)

            `when`("make active") {

                user.makeActive()

                then("has active flag") {

                    user.active() shouldBe true
                }

            }

        }

        given("active user to make deactivate it") {

            val user = User(id, null, null, true)

            `when`("deactivate user") {

                user.deactivate()

                then("has false active flag") {

                    user.active() shouldBe false
                }

            }

        }

    }

})
