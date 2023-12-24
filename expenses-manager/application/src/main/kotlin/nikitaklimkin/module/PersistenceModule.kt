package nikitaklimkin.module

import me.nikitaklimkin.access.UserExtractor
import me.nikitaklimkin.access.UserPersistence
import me.nikitaklimkin.configuration.DataBaseProperties
import me.nikitaklimkin.repository.ExpensesRepository
import me.nikitaklimkin.repository.UserRepository
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