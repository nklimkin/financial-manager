package me.nikitaklimkin.application.module

import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.expenses.repository.ExpensesRepository
import me.nikitaklimkin.persistence.user.repository.UserRepository
import me.nikitaklimkin.useCase.access.UserExtractor
import me.nikitaklimkin.useCase.access.UserPersistence
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module
import org.litote.kmongo.KMongo

fun buildPersistenceModule(properties: PersistenceModuleProperties): Module {
    return module(createdAtStart = true) {
        factory {
            KMongo.createClient(properties.dataSourceUrl)
        }
        single {
            DataBaseProperties(
                dataBaseName = properties.dataSourceDataBaseName
            )
        }
        single { ExpensesRepository(get(), get()) }
        single { UserRepository(get(), get()) }.binds(arrayOf(UserPersistence::class, UserExtractor::class))
    }
}

data class PersistenceModuleProperties(
    val dataSourceUrl: String,
    val dataSourceDataBaseName: String
)