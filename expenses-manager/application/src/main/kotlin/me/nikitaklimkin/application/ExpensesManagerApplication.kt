package me.nikitaklimkin.application

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.nikitaklimkin.application.plugin.configureKoinDI
import me.nikitaklimkin.rest.plugin.configureRouting
import me.nikitaklimkin.rest.plugin.configureSerialization

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoinDI()
    configureRouting()
    configureSerialization()
}