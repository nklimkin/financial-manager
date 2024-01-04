package me.nikitaklimkin.domain

import java.util.*

const val VALID_USER_NAME = "unique-user-1"
const val INVALID_USER_NAME = ""
const val TELEGRAM_CHAT_ID = 1L
const val TB_VALID_USER_NAME = "tb-user-name"

val USER_ID = UserId(UUID.randomUUID())
val USER_NAME = UserName.create(VALID_USER_NAME).getOrNull()!!
val TB_USER_NAME = UserName.create(TB_VALID_USER_NAME).getOrNull()!!

fun buildUser(): User {
    val user = User.build(
        id = UserId(UUID.randomUUID()),
        userName = UserName.create(VALID_USER_NAME).getOrNull()!!,
        telegramUser = buildTelegramUser(),
        active = true
    )
    return user.getOrNull()!!
}

fun buildTelegramUser() = TelegramUser(
    chatId = TELEGRAM_CHAT_ID,
    userName = UserName.create(VALID_USER_NAME).getOrNull()!!
)