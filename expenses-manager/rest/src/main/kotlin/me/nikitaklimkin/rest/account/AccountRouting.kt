package me.nikitaklimkin.rest.account

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        accountRoute()
    }
}