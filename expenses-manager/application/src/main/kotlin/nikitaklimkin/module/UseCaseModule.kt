package nikitaklimkin.module

import me.nikitaklimkin.AddNewUser
import me.nikitaklimkin.impl.AddNewUserUseCase
import org.koin.dsl.module

val useCasesModule = module(createdAtStart = true) {
    single<AddNewUser> { AddNewUserUseCase(get()) }
}