package me.nikitaklimkin.domain.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.model.ValueObject

data class Interest internal constructor(val value: Double) : ValueObject {

    companion object {

        fun from(value: Double): Either<InterestCreateError, Interest> {
            return if (value < 0) {
                InterestCreateError.left()
            } else {
                Interest(value).right();
            }
        }
    }
}

object InterestCreateError : DomainError()