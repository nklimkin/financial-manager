package me.nikitaklimkin.persistence.user.model

import arrow.core.Either
import arrow.core.raise.either
import me.nikitaklimkin.domain.user.*
import me.nikitaklimkin.persistence.common.model.PersistenceModel
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import java.time.OffsetDateTime
import java.util.*

data class UserPersistenceModel(
    @BsonId
    override val id: Id<UserPersistenceModel>,
    val oathId: String,
    val userName: String,
    val active: Boolean,
    val created: OffsetDateTime
) : PersistenceModel(id) {

    companion object {

        fun fromBusiness(user: User): UserPersistenceModel {
            return UserPersistenceModel(
                id = user.id.toPersistenceId(),
                oathId = user.oauthId.value,
                userName = user.userName().getValue(),
                active = user.active(),
                created = user.created
            )
        }

    }

    fun toBusiness(): Either<CreateUserError, User> {
        return either {
            User.restore(
                this@UserPersistenceModel.id.toUserId(),
                OauthId.from(this@UserPersistenceModel.oathId).bind(),
                UserName.from(this@UserPersistenceModel.userName).bind(),
                active,
                created
            ).bind()
        }
            .mapLeft { CreateUserError.InvalidUser }
    }

}

fun UserId.toPersistenceId(): Id<UserPersistenceModel> {
    return this.toUuid().toString().toId()
}

fun Id<UserPersistenceModel>.toUserId(): UserId {
    return UserId(UUID.fromString(this.toString()))
}