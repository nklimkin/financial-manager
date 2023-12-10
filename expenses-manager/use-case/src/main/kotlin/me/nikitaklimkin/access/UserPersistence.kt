package me.nikitaklimkin.access

import me.nikitaklimkin.User

interface UserPersistence {

    fun save(user: User)

}