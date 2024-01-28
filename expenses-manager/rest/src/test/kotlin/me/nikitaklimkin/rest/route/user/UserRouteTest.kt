package me.nikitaklimkin.rest.route.user

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import me.nikitaklimkin.rest.INVALID_ADD_SIMPLE_USER_BODY
import me.nikitaklimkin.rest.INVALID_ADD_TELEGRAM_USER_BODY
import me.nikitaklimkin.rest.VALID_ADD_SIMPLE_USER_BODY
import me.nikitaklimkin.rest.VALID_ADD_TELEGRAM_USER_BODY
import me.nikitaklimkin.rest.user.configureRouting
import me.nikitaklimkin.rest.plugin.configureSerialization
import me.nikitaklimkin.useCase.user.AddNewUser
import me.nikitaklimkin.useCase.user.access.UserExtractor
import me.nikitaklimkin.useCase.user.access.UserPersistence
import me.nikitaklimkin.useCase.user.impl.AddNewUserUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension

class UserRouteTest : KoinTest {

    @JvmField
    @RegisterExtension
    val routeTestExtension = KoinTestExtension.create {
        modules(
            module {
                // TODO заменить на мок use-case
                single<UserPersistence> { mockk<UserPersistence>(relaxed = true) }
                single<UserExtractor> { mockk<UserExtractor>(relaxed = true) }
                single<AddNewUser> { AddNewUserUseCase(get(), get()) }
            }
        )
    }

    @Test
    fun `when send request to save user with valid arguments then has match result`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/api/v1/user") {
            contentType(ContentType.Application.Json)
            setBody(VALID_ADD_SIMPLE_USER_BODY)
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when send request with invalid user name when has match result`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/api/v1/user") {
            contentType(ContentType.Application.Json)
            setBody(INVALID_ADD_SIMPLE_USER_BODY)
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when send request to save user by telegram when has match result`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/api/v1/user/telegram") {
            contentType(ContentType.Application.Json)
            setBody(VALID_ADD_TELEGRAM_USER_BODY)
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when send request to save user by invalid telegram info when has match result`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/api/v1/user/telegram") {
            contentType(ContentType.Application.Json)
            setBody(INVALID_ADD_TELEGRAM_USER_BODY)
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }
}