package me.nikitaklimkin.application.module

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import me.nikitaklimkin.rest.auth.oauth.ExternalUserInfoExtractor
import me.nikitaklimkin.rest.auth.oauth.GoogleExternalUserInfoExtractor
import org.koin.dsl.module

val restModule = module(createdAtStart = true) {
    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
    single<ExternalUserInfoExtractor> { GoogleExternalUserInfoExtractor(get()) }
}

