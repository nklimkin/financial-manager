package me.nikitaklimkin.rest.plugin

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.nikitaklimkin.rest.route.userRoute

fun Application.configureRouting() {
    routing {
        userRoute()
    }
}