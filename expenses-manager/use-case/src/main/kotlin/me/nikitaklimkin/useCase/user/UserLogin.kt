package me.nikitaklimkin.useCase.user

import arrow.core.Either
import me.nikitaklimkin.domain.user.OauthId
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.user.UserName

interface UserLogin {

    fun loginByOauth(request: OauthLoginUserRequest): Either<UserLoginError, UserId>
}

data class OauthLoginUserRequest(val oauthId: OauthId, val userName: UserName)

class UserLoginError