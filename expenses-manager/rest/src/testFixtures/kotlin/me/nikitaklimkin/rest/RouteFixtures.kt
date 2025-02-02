package me.nikitaklimkin.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.rest.login.UserSession

fun Application.configureTestSession() {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session -> session }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    routing {
        get("/test-login") {
            call.sessions.set(UserSession(USER_ID.toUuid().toString()))
            call.respond(HttpStatusCode.OK)
        }
    }

}