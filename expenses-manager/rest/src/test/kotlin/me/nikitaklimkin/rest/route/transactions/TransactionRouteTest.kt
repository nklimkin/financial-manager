package me.nikitaklimkin.rest.route.transactions

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.nikitaklimkin.domain.ACCOUNT_ID
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.buildTransactionDto
import me.nikitaklimkin.domain.buildTransactionId
import me.nikitaklimkin.domain.transaction.dto.TransactionDTO
import me.nikitaklimkin.rest.*
import me.nikitaklimkin.rest.account.configureAccountRouting
import me.nikitaklimkin.rest.plugin.configureSerialization
import me.nikitaklimkin.rest.route.buildSession
import me.nikitaklimkin.rest.transaction.configureTransactionRouting
import me.nikitaklimkin.rest.transaction.dto.TransactionRestResponse
import me.nikitaklimkin.rest.transaction.dto.toDetails
import me.nikitaklimkin.useCase.transaction.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class TransactionRouteTest : KoinTest {

    @JvmField
    @RegisterExtension
    val routeTestExtension = KoinTestExtension.create {
        modules(
            module {
                single<GetTransactions> { mockk<GetTransactions>(relaxed = true) }
                single<AddNewTransaction> { mockk<AddNewTransaction>(relaxed = true) }
                single<RemoveTransaction> { mockk<RemoveTransaction>(relaxed = true) }
                single<UpdateTransaction> { mockk<UpdateTransaction>(relaxed = true) }
            }
        )
    }

    private val addNewTransaction: AddNewTransaction by inject()
    private val updateTransaction: UpdateTransaction by inject()
    private val getTransactions: GetTransactions by inject()
    private val removeTransaction: RemoveTransaction by inject()

    @Test
    fun `when add new valid transaction then has success response`(): Unit = testApplication() {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        every { addNewTransaction.execute(any()) } returns Unit.right()

        val session = buildSession()

        val response = client.post("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody(Json.encodeToString(buildValidAddTransactionRestRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when add new invalid transaction then has error response`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        val session = buildSession()

        val response = client.post("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody(Json.encodeToString(buildInvalidAddTransactionRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new transaction but there is error while process then has error response`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        every { addNewTransaction.execute(any()) } returns AddNewTransactionError.AccountNotFound.left()

        val session = buildSession()

        val response = client.post("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody(Json.encodeToString(buildValidAddTransactionRestRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when have invalid request body while add new transaction then has error response`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        every { addNewTransaction.execute(any()) } returns Unit.right()

        val session = buildSession()

        val response = client.post("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody("""{"a":1}""")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when update transaction with valid request then has success response`(): Unit = testApplication() {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        every { updateTransaction.execute(any()) } returns Unit.right()

        val session = buildSession()

        val response = client.put("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody(Json.encodeToString(buildValidUpdateTransactionRestRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when update transaction with invalid request then has error response`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        val session = buildSession()

        val response = client.put("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody(Json.encodeToString(buildInvalidUpdateTransactionRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when update transaction but there is error while process then has error response`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        every { updateTransaction.execute(any()) } returns UpdateTransactionError.TransactionNotFound.left()

        val session = buildSession()

        val response = client.put("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody(Json.encodeToString(buildValidUpdateTransactionRestRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when have invalid request body while update transaction then has error response`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        val session = buildSession()

        val response = client.put("/api/v1/transactions") {
            contentType(ContentType.Application.Json)
            cookie("user_session", session)
            setBody("""{"a":1}""")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when get transactions by account id and user id then has match result`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }
        val userId = USER_ID
        val accountId = ACCOUNT_ID
        val transactionDTOS: List<TransactionDTO> = listOf(buildTransactionDto(), buildTransactionDto())
        every { getTransactions.execute(GetTransactionsDTO(userId, accountId)) } returns transactionDTOS.right()

        val session = buildSession()

        val response =
            client.get("/api/v1/transactions/account/${accountId.toUuid()}") {
                contentType(ContentType.Application.Json)
                cookie("user_session", session)
            }

        response.status shouldBe HttpStatusCode.OK
        val body = Json.decodeFromString<List<TransactionRestResponse>>(response.bodyAsText())
        body.size shouldBe 2
    }

    @Test
    fun `when get transaction by unknown user id or account id then has error result`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }
        val userId = USER_ID
        val accountId = ACCOUNT_ID
        every {
            getTransactions.execute(
                GetTransactionsDTO(
                    userId,
                    accountId
                )
            )
        } returns GetTransactionsError.TransactionsNotFound.left()

        val session = buildSession()

        val response =
            client.get("/api/v1/transactions/account/${accountId.toUuid()}") {
                contentType(ContentType.Application.Json)
                cookie("user_session", session)
            }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when get transaction by valid user id and invalid account then has error result`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        val session = buildSession()

        val response =
            client.get("/api/v1/transactions/account/1234") {
                contentType(ContentType.Application.Json)
                cookie("user_session", session)
            }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when map dto to details then has match result`() {
        val dto = buildTransactionDto()

        val result = dto.toDetails()

        result.id shouldBe dto.id.toString()
        result.name shouldBe dto.name.toStringValue()
        result.amount shouldBe dto.amount.value.toDouble()
        result.type shouldBe dto.type.toStringValue()
        result.direction shouldBe dto.direction.toString()
        result.created shouldBe dto.created
        result.description shouldBe dto.description
    }

    @Test
    fun `when delete valid transaction then has success result`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }
        val id = buildTransactionId()
        val userId = USER_ID
        every { removeTransaction.execute(DeleteTransactionDTO(userId, id)) } returns Unit.right()

        val session = buildSession()

        val response = client.delete("/api/v1/transactions/${id.toUuid()}") {
            cookie("user_session", session)
        }

        response.status shouldBe HttpStatusCode.NoContent
    }

    @Test
    fun `when delete transaction by invalid id then has error`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }

        val session = buildSession()

        val response = client.delete("/api/v1/transactions/1234") {
            cookie("user_session", session)
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when delete transaction by unknown id then has error`() = testApplication {
        application {
            configureTestSession()
            configureTransactionRouting()
            configureSerialization()
        }
        val id = buildTransactionId()
        val userId = USER_ID
        every {
            removeTransaction.execute(
                DeleteTransactionDTO(
                    userId,
                    id
                )
            )
        } returns RemoveTransactionError.TransactionNotFound.left()

        val session = buildSession()

        val response = client.delete("/api/v1/transactions/${id.toUuid()}") {
            cookie("user_session", session)
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @TestFactory
    fun `when Send Any Request Without Session Then Has 401 Error`() = listOf(
        HttpMethod.Post to "/api/v1/transactions",
        HttpMethod.Put to "/api/v1/transactions",
        HttpMethod.Get to "/api/v1/transactions/account/1234",
        HttpMethod.Delete to "/api/v1/transactions/1234",
    )
        .map { (method, url) ->
            DynamicTest.dynamicTest(
                "when process request with url = ${url} and method = ${method}"
            ) {
                testApplication {
                    application {
                        configureTestSession()
                        configureTransactionRouting()
                        configureSerialization()
                    }

                    val response = client.request(url) {
                        this.method = method
                    }

                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }
}