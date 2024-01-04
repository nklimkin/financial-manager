package me.nikitaklimkin.application.module

import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.repository.ExpensesRepository
import me.nikitaklimkin.persistence.repository.UserRepository
import me.nikitaklimkin.useCase.access.UserExtractor
import me.nikitaklimkin.useCase.access.UserPersistence
import org.koin.dsl.binds
import org.koin.dsl.module
import org.litote.kmongo.KMongo

val persistenceModule = module(createdAtStart = true) {
    factory {
        val uri = "mongodb://kotlin:kotlin@0.0.0.0:27017"
        KMongo.createClient(uri)
    }
    single {
        DataBaseProperties(
            dataBaseName = "expenses-manager"
        )
    }
    single { ExpensesRepository(get(), get()) }
    single { UserRepository(get(), get()) }.binds(arrayOf(UserPersistence::class, UserExtractor::class))
}