package me.nikitaklimkin.rest.login

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Application.configureLoginRouting() {

    val httpClient: HttpClient by inject()

    install(Sessions) {
        cookie<UserSession>("user_session")
    }

    install(Authentication) {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("FINANCIAL_MANAGER_GOOGLE_OAUTH_CLIENT_ID"),
                    clientSecret = System.getenv("FINANCIAL_MANAGER_GOOGLE_OAUTH_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                    extraAuthParameters = listOf("access_type" to "offline")
                )
            }
            client = httpClient
        }
        session<UserSession>("auth-session") {
            validate { session -> session }
            challenge {
                call.respondRedirect(API_V1_LOGIN)
            }
        }
    }

    routing {
        authenticate("auth-oauth-google") {
            loginRoute()
        }
    }
}

@Serializable
data class UserSession(val userId: String) : Principal