package me.nikitaklimkin.rest.user

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureUserRouting() {
    routing {
        authenticate("auth-session") {
            userRoute()
        }
    }
}