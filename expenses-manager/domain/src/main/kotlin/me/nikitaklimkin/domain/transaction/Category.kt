package me.nikitaklimkin.domain.transaction

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class Category internal constructor(private val type: String) : ValueObject {

    companion object {

        fun from(type: String): Either<NotValidCategory, Category> {
            if (type.isBlank()) {
                return NotValidCategory.left()
            }
            return Category(type).right()
        }

    }

    fun toStringValue() = type.toString()

}

object NotValidCategory : DomainError()