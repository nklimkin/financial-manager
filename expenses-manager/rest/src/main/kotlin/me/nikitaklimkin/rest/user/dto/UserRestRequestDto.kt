package me.nikitaklimkin.rest.user.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddUserRestRequest(
    val userName: String
)