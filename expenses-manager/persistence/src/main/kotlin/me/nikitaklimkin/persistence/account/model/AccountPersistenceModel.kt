package me.nikitaklimkin.persistence.account.model

import arrow.core.Either
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.account.CreateAccountIdError
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.toId

class AccountPersistenceModel(
    @BsonId
    id: Id<AccountPersistenceModel>
) {
}

fun Id<AccountPersistenceModel>.toAccountId(): Either<CreateAccountIdError, AccountId> {
    return AccountId.from(this.toString())
}

fun AccountId.toPersistenceId(): Id<AccountPersistenceModel> {
    return this.toUuid().toString().toId()
}