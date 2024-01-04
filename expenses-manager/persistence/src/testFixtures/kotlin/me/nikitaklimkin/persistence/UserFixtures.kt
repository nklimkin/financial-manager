package me.nikitaklimkin.persistence

import me.nikitaklimkin.domain.TB_VALID_USER_NAME
import me.nikitaklimkin.domain.TELEGRAM_CHAT_ID
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.VALID_USER_NAME
import me.nikitaklimkin.persistence.model.TelegramUserPersistenceModel
import me.nikitaklimkin.persistence.model.UserPersistenceModel
import me.nikitaklimkin.persistence.model.toPersistenceId

fun buildUserPersistenceModel() = UserPersistenceModel(
    USER_ID.toPersistenceId(),
    VALID_USER_NAME,
    null,
    true
)

fun buildUserPersistenceModelWithTbInfo() = UserPersistenceModel(
    USER_ID.toPersistenceId(),
    VALID_USER_NAME,
    buildTelegramUserPersistenceModel(),
    true
)

fun buildTelegramUserPersistenceModel() = TelegramUserPersistenceModel(
    TELEGRAM_CHAT_ID,
    TB_VALID_USER_NAME
)