package me.nikitaklimkin.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import me.nikitaklimkin.*
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

data class UserPersistenceModel(
    @BsonId
    override val id: UUID = UUID.randomUUID(),
    val userName: String?,
    val telegramUser: TelegramUserPersistenceModel?,
    val active: Boolean
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(user: User): UserPersistenceModel {
            return UserPersistenceModel(
                id = user.id.toUuid(),
                userName = user.userName()?.getValue(),
                telegramUser = TelegramUserPersistenceModel.fromBusiness(user.telegramUser()),
                active = user.active()
            )
        }

    }

    fun toBusiness(): Either<CreateUserError, User> {
        val telegramUserResult = telegramUser?.toBusiness()
        if (telegramUserResult != null && telegramUserResult.isLeft()) {
            return telegramUserResult.leftOrNull()!!.left()
        }
        val telegramUser = telegramUserResult?.getOrNull()
        return if (userName == null) {
            toBusinessWithoutUserName(
                UserId(id),
                telegramUser,
                active
            )
        } else {
            toBusinessWithFullUserInfo(
                UserId(id),
                userName,
                telegramUser,
                active
            )
        }
    }

    private fun toBusinessWithoutUserName(
        id: UserId,
        telegramUser: TelegramUser?,
        active: Boolean
    ): Either<CreateUserError, User> {
        return User.build(
            id,
            null,
            telegramUser,
            active
        )
    }

    private fun toBusinessWithFullUserInfo(
        id: UserId,
        userName: String,
        telegramUser: TelegramUser?,
        active: Boolean
    ): Either<CreateUserError, User> {
        return UserName.create(userName)
            .flatMap { validUserName ->
                User.build(
                    id,
                    validUserName,
                    telegramUser,
                    active
                )
            }
    }

}

data class TelegramUserPersistenceModel(
    val chatId: Long,
    val userName: String
) {
    companion object {

        fun fromBusiness(telegramUser: TelegramUser?): TelegramUserPersistenceModel? {
            if (telegramUser == null) {
                return null
            }
            return TelegramUserPersistenceModel(
                telegramUser.chatId,
                telegramUser.userName.getValue()
            )
        }

    }

    fun toBusiness(): Either<CreateUserError, TelegramUser> {
        return UserName.create(userName)
            .map { un ->
                TelegramUser(
                    chatId,
                    un
                )
            }
    }


}