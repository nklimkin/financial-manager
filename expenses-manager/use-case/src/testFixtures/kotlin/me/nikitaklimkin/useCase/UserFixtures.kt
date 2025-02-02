package me.nikitaklimkin.useCase

import me.nikitaklimkin.domain.USER_NAME
import me.nikitaklimkin.domain.USER_OAUTH_ID
import me.nikitaklimkin.useCase.user.OauthLoginUserRequest

fun buildValidOauthLoginUserRequest() = OauthLoginUserRequest(USER_OAUTH_ID, USER_NAME)