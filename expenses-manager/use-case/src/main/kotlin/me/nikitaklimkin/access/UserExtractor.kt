package me.nikitaklimkin.access

import arrow.core.Either
import me.nikitaklimkin.User
import me.nikitaklimkin.model.DomainError

interface UserExtractor {

    fun findByUserName(userName: String): Either<DomainError, User>

    fun findByTelegramChatId(chatId: Long): Either<DomainError, User>
}

class UserNotFound : DomainError()

