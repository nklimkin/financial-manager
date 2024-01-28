package me.nikitaklimkin.rest.expenses

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        expensesRoute()
    }
}