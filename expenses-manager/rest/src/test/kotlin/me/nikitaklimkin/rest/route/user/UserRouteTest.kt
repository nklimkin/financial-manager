package me.nikitaklimkin.rest.route.user

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import me.nikitaklimkin.rest.INVALID_ADD_SIMPLE_USER_BODY
import me.nikitaklimkin.rest.VALID_ADD_SIMPLE_USER_BODY
import me.nikitaklimkin.rest.plugin.configureSerialization
import me.nikitaklimkin.rest.user.configureRouting
import me.nikitaklimkin.useCase.user.AddNewUser
import me.nikitaklimkin.useCase.user.AddNewUserUseCaseError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class UserRouteTest : KoinTest {

    @JvmField
    @RegisterExtension
    val routeTestExtension = KoinTestExtension.create {
        modules(
            module {
                single<AddNewUser> { mockk() }
            }
        )
    }

    private val addNewUser: AddNewUser by inject()

    @Test
    fun `when send request to save user with valid arguments then has match result`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addNewUser.executeBySimpleInfo(any()) } returns Unit.right()

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
    fun `when add new user with use case error then has match result`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addNewUser.executeBySimpleInfo(any()) } returns AddNewUserUseCaseError().left()

        val response = client.post("/api/v1/user") {
            contentType(ContentType.Application.Json)
            setBody(VALID_ADD_SIMPLE_USER_BODY)
        }

        response.status shouldBe HttpStatusCode.InternalServerError
    }
}