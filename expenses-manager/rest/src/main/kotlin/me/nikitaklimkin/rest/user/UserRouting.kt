package me.nikitaklimkin.rest.user

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRoute()
    }
}