package me.nikitaklimkin.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.AggregateRoot
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject
import java.util.*

data class UserId(private val value: UUID) : ValueObject {
    fun toUuid() = value;
    override fun toString() = value.toString()

}

data class UserName internal constructor(private val value: String) : ValueObject {

    fun getValue() = value

    companion object {

        fun create(name: String): Either<CreateUserError, UserName> {
            if (name.isBlank()) {
                return CreateUserError.UserNameEmpty.left()
            }
            return UserName(name).right()
        }

    }

}

data class TelegramUser(
    val chatId: Long,
    val userName: UserName
)

class User internal constructor(
    val id: UserId,
    private var userName: UserName?,
    private var telegramUser: TelegramUser?,
    private var active: Boolean,
) : AggregateRoot<UserId>(id) {

    fun userName() = userName
    fun telegramUser() = telegramUser
    fun active() = active

    companion object {
        fun buildNewByTelegram(
            id: UserId,
            chatId: Long,
            telegramUserName: UserName
        ): Either<CreateUserError, User> {
            return User(
                id,
                null,
                TelegramUser(chatId, telegramUserName),
                true,
            ).right()
        }

        fun buildNew(
            id: UserId,
            userName: UserName
        ): Either<CreateUserError, User> {
            return User(
                id,
                userName,
                null,
                true,
            ).right()
        }

        fun build(
            id: UserId,
            userName: UserName?,
            telegramUser: TelegramUser?,
            active: Boolean
        ): Either<CreateUserError, User> {
            return User(
                id,
                userName,
                telegramUser,
                active
            ).right()
        }
    }

    fun updateByTelegramInfo(
        chatId: Long,
        userName: UserName
    ): Either<CreateUserError, Unit> {
        if (this.telegramUser != null) {
            return CreateUserError.TelegramInfoAlreadyExists.left()
        }
        this.telegramUser = TelegramUser(
            chatId,
            userName
        )
        return Unit.right()
    }

    fun updateByUserName(
        userName: UserName
    ): Either<CreateUserError, Unit> {
        if (this.userName != null) {
            return CreateUserError.UserNameInfoAlreadyExists.left()
        }
        this.userName = userName
        return Unit.right()
    }

    fun makeActive() {
        active = true
    }

    fun deactivate() {
        active = false
    }

}

sealed class CreateUserError : DomainError() {

    object UserNameEmpty : CreateUserError()

    object UserNameInfoAlreadyExists : CreateUserError()

    object TelegramInfoAlreadyExists : CreateUserError()

}