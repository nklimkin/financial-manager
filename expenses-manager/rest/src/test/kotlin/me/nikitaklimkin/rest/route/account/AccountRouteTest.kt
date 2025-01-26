package me.nikitaklimkin.rest.route.account

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
import me.nikitaklimkin.domain.TEST_ACCOUNT_DESCRIPTION
import me.nikitaklimkin.domain.TEST_BANK_NAME
import me.nikitaklimkin.rest.*
import me.nikitaklimkin.rest.account.configureRouting
import me.nikitaklimkin.rest.account.dto.AccountRestResponseDto
import me.nikitaklimkin.rest.plugin.configureSerialization
import me.nikitaklimkin.useCase.account.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.util.*

class AccountRouteTest : KoinTest {

    @JvmField
    @RegisterExtension
    val routeTestExtension = KoinTestExtension.create {
        modules(
            module {
                single<GetAccounts> { mockk<GetAccounts>(relaxed = true) }
                single<AddAccount> { mockk<AddAccount>(relaxed = true) }
                single<RemoveAccount> { mockk<RemoveAccount>(relaxed = true) }
                single<UpdateAccount> { mockk<UpdateAccount>(relaxed = true) }
            }
        )
    }

    private val addAccount: AddAccount by inject()
    private val updateAccount: UpdateAccount by inject()
    private val getAccounts: GetAccounts by inject()
    private val removeAccount: RemoveAccount by inject()

    @Test
    fun `when add new deposit account then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addDepositAccount(any()) } returns Unit.right()

        val response = client.post("/api/v1/accounts/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddDepositAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when add new deposit account with account exists result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addDepositAccount(any()) } returns AddNewAccountError.AccountAlreadyExists.left()

        val response = client.post("/api/v1/accounts/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddDepositAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new deposit account with user not found result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addDepositAccount(any()) } returns AddNewAccountError.UserNotFound.left()

        val response = client.post("/api/v1/accounts/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddDepositAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when add new deposit account with not valid request result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidAddDepositAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new deposit account with invalid body result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/deposit") {
            contentType(ContentType.Application.Json)
            setBody("some-body")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new card account then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addCardAccount(any()) } returns Unit.right()

        val response = client.post("/api/v1/accounts/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddCardAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when add new card account with account exists result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addCardAccount(any()) } returns AddNewAccountError.AccountAlreadyExists.left()

        val response = client.post("/api/v1/accounts/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddCardAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new card account with user not found result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addCardAccount(any()) } returns AddNewAccountError.UserNotFound.left()

        val response = client.post("/api/v1/accounts/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddCardAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when add new card account with not valid request result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidAddCardAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new card account with invalid body result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/card") {
            contentType(ContentType.Application.Json)
            setBody("some-body")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new broker account then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addBrokerAccount(any()) } returns Unit.right()

        val response = client.post("/api/v1/accounts/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddBrokerageAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when add new broker account with account exists result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addBrokerAccount(any()) } returns AddNewAccountError.AccountAlreadyExists.left()

        val response = client.post("/api/v1/accounts/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddBrokerageAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new broker account with user not found result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addBrokerAccount(any()) } returns AddNewAccountError.UserNotFound.left()

        val response = client.post("/api/v1/accounts/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddBrokerageAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when add new broker account with not valid request result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidAddBrokerageAccountRestRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new broker account with invalid body result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/broker") {
            contentType(ContentType.Application.Json)
            setBody("some-body")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new piggy account then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addPiggyAccount(any()) } returns Unit.right()

        val response = client.post("/api/v1/accounts/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddPiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when add new piggy account with account exists result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addPiggyAccount(any()) } returns AddNewAccountError.AccountAlreadyExists.left()

        val response = client.post("/api/v1/accounts/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddPiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new piggy account with user not found result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { addAccount.addPiggyAccount(any()) } returns AddNewAccountError.UserNotFound.left()

        val response = client.post("/api/v1/accounts/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildAddPiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when add new piggy account with not valid request result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidAddPiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when add new piggy account with invalid body result then has match code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/api/v1/accounts/piggy") {
            contentType(ContentType.Application.Json)
            setBody("some-body")
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when get accounts by existed user then has match result`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { getAccounts.execute(any()) } returns buildGetAccountResponse().right()

        val response = client.get("/api/v1/accounts/${UUID.randomUUID().toString()}")

        response.status shouldBe HttpStatusCode.OK
        val body = Json.decodeFromString<List<AccountRestResponseDto>>(response.bodyAsText())
        body.size shouldBe 1
        body.first().accountId shouldBe ACCOUNT_ID.toUuid().toString()
        body.first().bankName shouldBe TEST_BANK_NAME.value
        body.first().description shouldBe TEST_ACCOUNT_DESCRIPTION.value
    }

    @Test
    fun `when get accounts for not existed user then has 404 code`(): Unit = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { getAccounts.execute(any()) } returns GetAccountError.UserNotFound.left()

        val response = client.get("/api/v1/accounts/${UUID.randomUUID().toString()}")

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when remove existed account then has 204 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { removeAccount.execute(any()) } returns Unit.right()

        val response = client.delete("/api/v1/accounts/${UUID.randomUUID()}/${UUID.randomUUID()}")

        response.status shouldBe HttpStatusCode.NoContent
    }

    @Test
    fun `when remove not existed account then has 400 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { removeAccount.execute(any()) } returns RemoveAccountError.AccountNotFound.left()

        val response = client.delete("/api/v1/accounts/${UUID.randomUUID()}/${UUID.randomUUID()}")

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when remove account for not existed user then has match result`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { removeAccount.execute(any()) } returns RemoveAccountError.UserNotFound.left()

        val response = client.delete("/api/v1/accounts/${UUID.randomUUID()}/${UUID.randomUUID()}")

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when update broker existed account by valid request then has 201 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateBrokerAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when update broker existed account by invalid request then has 400 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidUpdateBrokerAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when update broker not existed account then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.AccountNotFound.left()

        val response = client.put("/api/v1/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateBrokerAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when update broker not existed user then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.UserNotFound.left()

        val response = client.put("/api/v1/broker") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateBrokerAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when update card existed account by valid request then has 201 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateCardAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when update card existed account by invalid request then has 400 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidUpdateCardAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when update card not existed account then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.AccountNotFound.left()

        val response = client.put("/api/v1/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateCardAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when update card not existed user then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.UserNotFound.left()

        val response = client.put("/api/v1/card") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateCardAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }


    @Test
    fun `when update deposit existed account by valid request then has 201 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateDepositAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when update deposit existed account by invalid request then has 400 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidUpdateDepositAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when update deposit not existed account then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.AccountNotFound.left()

        val response = client.put("/api/v1/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateDepositAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when update deposit not existed user then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.UserNotFound.left()

        val response = client.put("/api/v1/deposit") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdateDepositAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when update piggy existed account by valid request then has 201 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdatePiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun `when update piggy existed account by invalid request then has 400 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns Unit.right()

        val response = client.put("/api/v1/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildInvalidUpdatePiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `when update piggy not existed account then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.AccountNotFound.left()

        val response = client.put("/api/v1/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdatePiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `when update piggy not existed user then has 404 code`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        every { updateAccount.execute(any()) } returns UpdateAccountError.UserNotFound.left()

        val response = client.put("/api/v1/piggy") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(buildValidUpdatePiggyAccountRequest()))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }
}