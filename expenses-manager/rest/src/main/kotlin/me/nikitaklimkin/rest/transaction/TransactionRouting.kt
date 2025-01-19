package me.nikitaklimkin.rest.transaction

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        transactionRoute()
    }
}