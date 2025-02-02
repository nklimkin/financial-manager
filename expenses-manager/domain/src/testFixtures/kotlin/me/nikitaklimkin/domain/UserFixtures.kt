package me.nikitaklimkin.domain

import me.nikitaklimkin.domain.user.*
import java.time.OffsetDateTime
import java.util.*

const val VALID_USER_NAME = "unique-user-1"
const val INVALID_USER_NAME = ""
const val TELEGRAM_CHAT_ID = 1L
const val TB_VALID_USER_NAME = "tb-user-name"

val USER_ID = UserId(UUID.randomUUID())
val USER_ID_2 = UserId(UUID.randomUUID())
val USER_NAME = UserName.from(VALID_USER_NAME).getOrNull()!!
val USER_NAME_2 = UserName.from("some-user-name").getOrNull()!!
val USER_OAUTH_ID = OauthId.from("123456").getOrNull()!!
val USER_OAUTH_ID_2 = OauthId.from("98765432").getOrNull()!!
val TB_USER_NAME = UserName.from(TB_VALID_USER_NAME).getOrNull()!!

fun buildUser(
    id: UserId = UserId(UUID.randomUUID()),
    created: OffsetDateTime = OffsetDateTime.now()
): User {
    val user = User.restore(
        id = id,
        oauthId = USER_OAUTH_ID,
        userName = UserName.from(VALID_USER_NAME).getOrNull()!!,
        active = true,
        created = created
    )
    return user.getOrNull()!!
}

class FixturesUserIdGenerator : UserIdGenerator {
    override fun generate(): UserId {
        return USER_ID
    }


}