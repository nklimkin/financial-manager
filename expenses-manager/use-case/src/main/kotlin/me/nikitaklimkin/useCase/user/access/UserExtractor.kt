package me.nikitaklimkin.useCase.user.access

import me.nikitaklimkin.domain.user.User
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.domain.user.UserName

interface UserExtractor {

    fun findByUserName(userName: UserName): User?

    fun findByUserId(userId: UserId): User?
}

