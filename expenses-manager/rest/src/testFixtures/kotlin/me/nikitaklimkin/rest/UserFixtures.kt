package me.nikitaklimkin.rest

import me.nikitaklimkin.domain.INVALID_USER_NAME
import me.nikitaklimkin.domain.VALID_USER_NAME

const val VALID_ADD_SIMPLE_USER_BODY = "{\"userName\":\"$VALID_USER_NAME\"}"
const val INVALID_ADD_SIMPLE_USER_BODY = "{\"userName\":\"$INVALID_USER_NAME\"}"