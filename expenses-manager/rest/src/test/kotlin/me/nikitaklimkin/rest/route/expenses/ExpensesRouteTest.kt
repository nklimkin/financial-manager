package me.nikitaklimkin.rest.route.expenses

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.nikitaklimkin.rest.buildInvalidAddExpensesRestRequest
import me.nikitaklimkin.rest.buildInvalidUpdateExpensesRestRequest
import me.nikitaklimkin.rest.buildValidAddExpensesRestRequest
import me.nikitaklimkin.rest.buildValidUpdateExpensesRestRequest
import me.nikitaklimkin.rest.expenses.configureRouting
import me.nikitaklimkin.rest.plugin.configureSerialization
import me.nikitaklimkin.useCase.expenses.*
import me.nikitaklimkin.useCase.expenses.access.ExpensesNotFoundError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class ExpensesRouteTest : KoinTest {

    @JvmField
    @RegisterExtension
    val routeTestExtension = KoinTestExtension.create {
        modules(
            module {
                single<GetExpenses> { mockk<GetExpenses>(relaxed = true) }
                single<AddNewExpenses> { mockk<AddNewExpenses>(relaxed = true) }
                single<DeleteExpenses> { mockk<DeleteExpenses>(relaxed = true) }
                single<UpdateExpenses> { mockk<UpdateExpenses>(relaxed = true) }
            }
        )
    }

    private val addNewExpenses: AddNewExpenses by inject()
    private val updateExpenses: UpdateExpenses by inject()

    @Test
    fun `when add new valid expenses then has success response`(): Unit = testApplication() {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addNewExpenses.execute(any()) } returns Unit.right()

        val response = client.post("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidAddExpensesRestRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when add new invalid expenses then has error response`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        val response = client.post("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidAddExpensesRestRequest()))
        }

        response.status shouldBe HttpStatusCode.InternalServerError
    }

    @Test
    fun `when add new expenses but there is error while process then has error response`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addNewExpenses.execute(any()) } returns ExpensesNotFoundError.left()

        val response = client.post("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidAddExpensesRestRequest()))
        }

        response.status shouldBe HttpStatusCode.InternalServerError
    }

    @Test
    fun `when have invalid request body while add new expenses then has error response`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addNewExpenses.execute(any()) } returns Unit.right()

        val response = client.post("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody("""{"a":1}""")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when update expenses with valid request then has success response`(): Unit = testApplication() {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateExpenses.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateExpensesRestRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when update expenses with invalid request then has error response`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        val response = client.put("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidUpdateExpensesRestRequest()))
        }

        response.status shouldBe HttpStatusCode.InternalServerError
    }

    @Test
    fun `when update expenses but there is error while process then has error response`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateExpenses.execute(any()) } returns UpdateExpensesError.left()

        val response = client.put("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateExpensesRestRequest()))
        }

        response.status shouldBe HttpStatusCode.InternalServerError
    }

    @Test
    fun `when have invalid request body while update expenses then has error response`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        val response = client.put("/api/v1/expenses") {
            contentType(ContentType.Application.Json)
            setBody("""{"a":1}""")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }
}