package me.nikitaklimkin.domain.user

interface UserIdGenerator {

    fun generate(): UserId
}