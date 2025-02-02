package me.nikitaklimkin.rest.transaction

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureTransactionRouting() {
    routing {
        authenticate("auth-session") {
            transactionRoute()
        }
    }
}