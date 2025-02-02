package me.nikitaklimkin.rest.auth.oauth

import arrow.core.Either
import arrow.core.raise.either
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.nikitaklimkin.domain.user.OauthId
import me.nikitaklimkin.domain.user.UserName
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class GoogleExternalUserInfoExtractor(
    private val httpClient: HttpClient
) : ExternalUserInfoExtractor {
    override suspend fun extract(token: OAuthAccessTokenResponse.OAuth2):
            Either<InvalidExternalInfoError, ExternalUserInfo> {
        log.debug { "Extract google user info by token" }
        val user: UserInfoDTO = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${token.accessToken}")
            }
        }.body()
        log.debug { "Extract google user with id = ${user.id}" }
        return either {
            val id = OauthId.from(user.id).bind()
            val userName = UserName.from(user.name).bind()
            ExternalUserInfo(id, userName)
        }
            .mapLeft { InvalidExternalInfoError }
    }
}

@Serializable
data class UserInfoDTO(
    val id: String,
    val name: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String,
    val picture: String
)