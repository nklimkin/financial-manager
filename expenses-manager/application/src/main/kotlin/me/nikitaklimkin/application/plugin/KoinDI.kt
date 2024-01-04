package me.nikitaklimkin.application.plugin

import io.ktor.server.application.*
import me.nikitaklimkin.application.module.persistenceModule
import me.nikitaklimkin.application.module.useCasesModule
import org.koin.ktor.plugin.Koin

fun Application.configureKoinDI() {
    install(Koin) {
        modules(
            persistenceModule,
            useCasesModule
        )
    }
}