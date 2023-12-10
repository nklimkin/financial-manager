package me.nikitaklimkin.plugin

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.nikitaklimkin.route.userRoute

fun Application.configureRouting() {
    routing {
        userRoute()
    }
}