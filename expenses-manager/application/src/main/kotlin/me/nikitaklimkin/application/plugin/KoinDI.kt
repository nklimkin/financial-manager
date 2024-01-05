package me.nikitaklimkin.application.plugin

import io.ktor.server.application.*
import me.nikitaklimkin.application.module.PersistenceModuleProperties
import me.nikitaklimkin.application.module.buildPersistenceModule
import me.nikitaklimkin.application.module.useCasesModule
import org.koin.ktor.plugin.Koin

fun Application.configureKoinDI() {
    install(Koin) {
        modules(
            buildPersistenceModule(
                PersistenceModuleProperties(
                    dataSourceUrl = environment.config.property("datasource.mongo.url").getString(),
                    dataSourceDataBaseName = environment.config.property("datasource.mongo.dataBase").getString()
                )
            ),
            useCasesModule
        )
    }
}