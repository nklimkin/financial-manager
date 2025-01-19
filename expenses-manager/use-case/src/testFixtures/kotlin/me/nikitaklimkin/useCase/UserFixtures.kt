package me.nikitaklimkin.useCase

import me.nikitaklimkin.domain.USER_NAME
import me.nikitaklimkin.useCase.user.AddSimpleUserRequest

fun buildValidAddSimpleUserRequest() = AddSimpleUserRequest(USER_NAME)