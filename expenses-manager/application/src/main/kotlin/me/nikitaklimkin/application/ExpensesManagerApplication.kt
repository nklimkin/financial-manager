package me.nikitaklimkin.application

import io.ktor.server.application.*
import io.ktor.server.netty.*
import me.nikitaklimkin.application.plugin.configureKoinDI
import me.nikitaklimkin.rest.account.configureAccountRouting
import me.nikitaklimkin.rest.login.configureLoginRouting
import me.nikitaklimkin.rest.plugin.configureSerialization
import me.nikitaklimkin.rest.transaction.configureTransactionRouting
import me.nikitaklimkin.rest.user.configureUserRouting

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureKoinDI()
    configureLoginRouting()
    configureUserRouting()
    configureTransactionRouting()
    configureAccountRouting()
    configureSerialization()
}