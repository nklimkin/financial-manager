package me.nikitaklimkin.model

import java.time.OffsetDateTime
import java.util.UUID

abstract class DomainEvent() {
    val id = DomainEventId.generate()
    val created = OffsetDateTime.now()
}

data class DomainEventId internal constructor(val id: UUID) {

    companion object {

        fun generate(): DomainEventId = DomainEventId(UUID.randomUUID())

    }

}