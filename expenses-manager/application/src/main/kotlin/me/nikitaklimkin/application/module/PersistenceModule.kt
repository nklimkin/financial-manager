package me.nikitaklimkin.application.module

import me.nikitaklimkin.domain.account.AccountIdGenerator
import me.nikitaklimkin.domain.transaction.TransactionIdGenerator
import me.nikitaklimkin.domain.user.UserIdGenerator
import me.nikitaklimkin.persistence.account.repository.AccountRepository
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.transactions.repository.TransactionsRepository
import me.nikitaklimkin.persistence.user.repository.UserRepository
import me.nikitaklimkin.useCase.account.access.AccountExtractor
import me.nikitaklimkin.useCase.account.access.AccountPersistence
import me.nikitaklimkin.useCase.transaction.access.TransactionExtractor
import me.nikitaklimkin.useCase.transaction.access.TransactionPersistence
import me.nikitaklimkin.useCase.user.access.UserExtractor
import me.nikitaklimkin.useCase.user.access.UserPersistence
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
        single { TransactionsRepository(get(), get()) }.binds(
            arrayOf(
                TransactionExtractor::class,
                TransactionPersistence::class,
                TransactionIdGenerator::class
            )
        )
        single { UserRepository(get(), get()) }.binds(
            arrayOf(
                UserPersistence::class,
                UserExtractor::class,
                UserIdGenerator::class
            )
        )
        single { AccountRepository(get(), get()) }.binds(
            arrayOf(
                AccountExtractor::class,
                AccountPersistence::class,
                AccountIdGenerator::class
            )
        )
    }
}

data class PersistenceModuleProperties(
    val dataSourceUrl: String,
    val dataSourceDataBaseName: String
)