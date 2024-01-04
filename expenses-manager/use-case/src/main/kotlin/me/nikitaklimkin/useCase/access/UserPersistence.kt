package me.nikitaklimkin.useCase.access

import me.nikitaklimkin.domain.User

interface UserPersistence {

    fun save(user: User)

}