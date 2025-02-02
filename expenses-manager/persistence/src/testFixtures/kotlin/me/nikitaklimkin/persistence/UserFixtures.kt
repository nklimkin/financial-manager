package me.nikitaklimkin.persistence

import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.USER_OAUTH_ID
import me.nikitaklimkin.domain.VALID_USER_NAME
import me.nikitaklimkin.persistence.user.model.UserPersistenceModel
import me.nikitaklimkin.persistence.user.model.toPersistenceId
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun buildUserPersistenceModel() = UserPersistenceModel(
    USER_ID.toPersistenceId(),
    USER_OAUTH_ID.value,
    VALID_USER_NAME,
    true,
    OffsetDateTime.of(
        LocalDateTime.of(2025, 1, 5, 10, 10, 10, 10),
        ZoneOffset.UTC
    )
)