package me.nikitaklimkin.rest.account

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureAccountRouting() {
    routing {
        authenticate("auth-session") {
            accountRoute()
        }
    }
}