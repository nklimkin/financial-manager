package me.nikitaklimkin.persistence.common.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

abstract class PersistenceModel(
    @BsonId
    open val id: Id<out PersistenceModel>
)