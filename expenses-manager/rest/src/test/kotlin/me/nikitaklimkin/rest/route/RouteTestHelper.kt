package me.nikitaklimkin.rest.route

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*

suspend fun ApplicationTestBuilder.buildSession(): String {
    val loginResponse = client.get("/test-login")
    loginResponse.status shouldBe HttpStatusCode.OK
    val session = loginResponse.setCookie()
        .first { cookie -> cookie.name == "user_session" }
        .value
    return session
}