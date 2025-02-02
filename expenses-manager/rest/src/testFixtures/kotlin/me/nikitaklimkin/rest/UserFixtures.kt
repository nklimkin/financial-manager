package me.nikitaklimkin.rest

import me.nikitaklimkin.domain.INVALID_USER_NAME
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.VALID_USER_NAME
import me.nikitaklimkin.rest.login.UserSession

const val VALID_ADD_SIMPLE_USER_BODY = "{\"userName\":\"$VALID_USER_NAME\"}"
const val INVALID_ADD_SIMPLE_USER_BODY = "{\"userName\":\"$INVALID_USER_NAME\"}"

fun buildTestSession(): UserSession = UserSession(USER_ID.toUuid().toString())