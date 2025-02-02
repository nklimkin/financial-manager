package me.nikitaklimkin.domain.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.AggregateRoot
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject
import java.time.OffsetDateTime
import java.util.*

data class UserId(private val value: UUID) : ValueObject {

    companion object {

        fun from(value: String): Either<CreateUserIdError, UserId> {
            return try {
                UserId(UUID.fromString(value)).right()
            } catch (exception: IllegalArgumentException) {
                CreateUserIdError.left()
            }
        }
    }

    fun toUuid() = value

}

data class OauthId internal constructor(val value: String) {

    companion object {

        fun from(id: String): Either<CreateOauthIdError, OauthId> {
            if (id.isBlank()) {
                return CreateOauthIdError.left()
            }
            return OauthId(id).right()
        }

    }
}

data class UserName internal constructor(private val value: String) : ValueObject {

    fun getValue() = value

    companion object {

        fun from(name: String): Either<CreateUserError, UserName> {
            if (name.isBlank()) {
                return CreateUserError.UserNameEmpty.left()
            }
            return UserName(name).right()
        }

    }

}

class User internal constructor(
    val id: UserId,
    val oauthId: OauthId,
    private var userName: UserName,
    private var active: Boolean,
    val created: OffsetDateTime
) : AggregateRoot<UserId>(id) {

    fun userName() = userName
    fun active() = active

    companion object {
        fun build(
            idGenerator: UserIdGenerator,
            oauthId: OauthId,
            userName: UserName
        ): Either<CreateUserError, User> {
            return User(
                idGenerator.generate(),
                oauthId,
                userName,
                true,
                OffsetDateTime.now(),
            ).right()
        }

        fun restore(
            id: UserId,
            oauthId: OauthId,
            userName: UserName,
            active: Boolean,
            created: OffsetDateTime
        ): Either<CreateUserError, User> {
            return User(
                id,
                oauthId,
                userName,
                active,
                created
            ).right()
        }
    }

    fun makeActive() {
        active = true
    }

    fun deactivate() {
        active = false
    }

}

data object CreateUserIdError : DomainError()

data object CreateOauthIdError : DomainError()

sealed class CreateUserError : DomainError() {

    data object UserNameEmpty : CreateUserError()

    data object UserNameInfoAlreadyExists : CreateUserError()

    data object InvalidUser : CreateUserError()

}