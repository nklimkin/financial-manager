package me.nikitaklimkin.useCase.access

import arrow.core.Either
import me.nikitaklimkin.domain.User
import me.nikitaklimkin.model.DomainError

interface UserExtractor {

    fun findByUserName(userName: String): Either<DomainError, User>

    fun findByTelegramChatId(chatId: Long): Either<DomainError, User>
}

object UserNotFound : DomainError()

