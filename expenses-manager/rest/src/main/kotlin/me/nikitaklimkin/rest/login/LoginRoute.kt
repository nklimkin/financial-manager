package me.nikitaklimkin.rest.login

import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import me.nikitaklimkin.rest.auth.oauth.ExternalUserInfoExtractor
import me.nikitaklimkin.useCase.user.OauthLoginUserRequest
import me.nikitaklimkin.useCase.user.UserLogin
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger {}

fun Route.loginRoute() {

    val externalUserInfoExtractor: ExternalUserInfoExtractor by inject()
    val userLogin: UserLogin by inject()

    route(API_V1_LOGIN) {
        get {
            // Redirects to 'authorizeUrl' automatically
        }
    }

    route(API_V1_CALLBACK) {
        get {
            val oauthToken: OAuthAccessTokenResponse.OAuth2 =
                call.principal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val userInfo = externalUserInfoExtractor.extract(oauthToken)
            if (userInfo.isLeft()) {
                log.error { "Can't extract user info from oauth server" }
                return@get call.respond(HttpStatusCode.InternalServerError)
            }
            either {
                val oauthUser = userInfo.bind()
                OauthLoginUserRequest(oauthUser.oauthId, oauthUser.userName)
            }
                .onLeft { call.respond(HttpStatusCode.InternalServerError) }
                .onRight { loginRequest ->
                    userLogin.loginByOauth(loginRequest)
                        .onLeft { call.respond(HttpStatusCode.InternalServerError) }
                        .onRight { userId ->
                            call.sessions.set(UserSession(userId.toUuid().toString()))
                            call.respondRedirect("/api/v1/accounts")
                        }
                }
        }
    }
}