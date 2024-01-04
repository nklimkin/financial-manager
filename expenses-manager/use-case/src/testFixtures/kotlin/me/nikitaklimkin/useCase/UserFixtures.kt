package me.nikitaklimkin.useCase

import me.nikitaklimkin.domain.INVALID_USER_NAME
import me.nikitaklimkin.domain.TELEGRAM_CHAT_ID
import me.nikitaklimkin.domain.VALID_USER_NAME

fun buildValidAddSimpleUserRequest() = AddSimpleUserRequest(VALID_USER_NAME)

fun buildInvalidAddSimpleUserRequest() = AddSimpleUserRequest(INVALID_USER_NAME)

fun buildValidAddTelegramUserRequest() = AddTelegramUserRequest(TELEGRAM_CHAT_ID, VALID_USER_NAME)

fun buildInvalidAddTelegramUserRequest() = AddTelegramUserRequest(TELEGRAM_CHAT_ID, INVALID_USER_NAME)