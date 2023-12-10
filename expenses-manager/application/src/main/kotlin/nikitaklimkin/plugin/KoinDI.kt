package nikitaklimkin.plugin

import io.ktor.server.application.*
import nikitaklimkin.module.persistenceModule
import nikitaklimkin.module.useCasesModule
import org.koin.ktor.plugin.Koin

fun Application.configureKoinDI() {
    install(Koin) {
        modules(
            persistenceModule,
            useCasesModule
        )
    }
}