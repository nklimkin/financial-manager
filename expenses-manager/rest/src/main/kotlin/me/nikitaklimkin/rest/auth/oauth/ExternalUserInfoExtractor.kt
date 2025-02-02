package me.nikitaklimkin.rest.auth.oauth

import arrow.core.Either
import io.ktor.server.auth.*
import me.nikitaklimkin.domain.user.OauthId
import me.nikitaklimkin.domain.user.UserName

interface ExternalUserInfoExtractor {

    suspend fun extract(token: OAuthAccessTokenResponse.OAuth2): Either<InvalidExternalInfoError, ExternalUserInfo>

}

data class ExternalUserInfo(
    val oauthId: OauthId,
    val userName: UserName
)

data object InvalidExternalInfoError