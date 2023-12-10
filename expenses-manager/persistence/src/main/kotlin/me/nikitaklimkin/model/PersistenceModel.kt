package me.nikitaklimkin.model

import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID

abstract class PersistenceModel(
    @BsonId
    open val id: UUID
) {

}