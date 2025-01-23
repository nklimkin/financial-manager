package me.nikitaklimkin.application.module

import me.nikitaklimkin.useCase.user.AddNewUser
import me.nikitaklimkin.useCase.user.impl.AddNewUserUseCase
import org.koin.dsl.module

val useCasesModule = module(createdAtStart = true) {
    single<AddNewUser> { AddNewUserUseCase(get(), get(), get()) }
}