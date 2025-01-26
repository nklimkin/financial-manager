package me.nikitaklimkin.rest.account

import arrow.core.Either
import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.AccountDescription
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.account.BankName
import me.nikitaklimkin.domain.account.Interest
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.model.DomainError
import me.nikitaklimkin.rest.account.dto.*
import me.nikitaklimkin.useCase.account.*
import me.nikitaklimkin.useCase.account.UpdateAccountRequest
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.math.BigDecimal
import java.util.function.BiFunction

private val log = KotlinLogging.logger { }

fun Route.accountRoute() {

    val addAccount: AddAccount by inject()
    val updateAccount: UpdateAccount by inject()
    val removeAccount: RemoveAccount by inject()
    val getAccounts: GetAccounts by inject()

    route(API_V1_ACCOUNTS) {

        post("/broker") {
            log.debug { "Add broker account request" }
            val body = call.receive<AddNewBrokerAccountRestRequest>()
            log.trace { "Receive request $body" }
            executeAddAccountRequest(
                either {
                    val userId = UserId.from(body.userId).bind()
                    val description = AccountDescription.from(body.description).bind()
                    val bankName = BankName.from(body.bankName).bind()
                    val balance = MoneyAmount.from(BigDecimal(body.initBalance))
                    AddNewBrokerAccountDTO(
                        userId,
                        bankName,
                        description,
                        balance
                    )
                },
                addAccount
            ) { addAccountElement, addRequest -> addAccountElement.addBrokerAccount(addRequest) }
        }

        post("/card") {
            log.debug { "Add card account request" }
            val body = call.receive<AddNewCardAccountRestRequest>()
            log.trace { "Receive request $body" }
            executeAddAccountRequest(
                either {
                    val userId = UserId.from(body.userId).bind()
                    val description = AccountDescription.from(body.description).bind()
                    val bankName = BankName.from(body.bankName).bind()
                    val balance = MoneyAmount.from(BigDecimal(body.initBalance))
                    AddNewCardAccountDTO(
                        userId,
                        bankName,
                        description,
                        balance
                    )
                },
                addAccount,
            ) { addAccountElement, addRequest -> addAccountElement.addCardAccount(addRequest) }
        }

        post("/deposit") {
            log.debug { "Add deposit account request" }
            val body = call.receive<AddNewDepositAccountRestRequest>()
            log.trace { "Receive request $body" }
            executeAddAccountRequest(
                either {
                    val userId = UserId.from(body.userId).bind()
                    val description = AccountDescription.from(body.description).bind()
                    val bankName = BankName.from(body.bankName).bind()
                    val initialBalance = MoneyAmount.from(BigDecimal(body.initialBalance))
                    val expectedBalance = MoneyAmount.from(BigDecimal(body.expectedFinalBalance))
                    val openedDate = body.openedDate
                    val closedDate = body.closedDate
                    val interest = Interest.from(body.interest).bind()
                    AddNewDepositAccountDTO(
                        userId,
                        bankName,
                        description,
                        initialBalance,
                        expectedBalance,
                        openedDate,
                        closedDate,
                        interest
                    )
                },
                addAccount,
            ) { addAccountElement, addRequest -> addAccountElement.addDepositAccount(addRequest) }
        }

        post("/piggy") {
            log.debug { "Add piggy account request" }
            val body = call.receive<AddNewPiggyAccountRestRequest>()
            log.trace { "Receive request $body" }
            executeAddAccountRequest(
                either {
                    val userId = UserId.from(body.userId).bind()
                    val description = AccountDescription.from(body.description).bind()
                    val bankName = BankName.from(body.bankName).bind()
                    val initialBalance = MoneyAmount.from(BigDecimal(body.initialBalance))
                    val interest = Interest.from(body.interest).bind()
                    AddNewPiggyAccountDTO(
                        userId,
                        bankName,
                        description,
                        initialBalance,
                        interest
                    )
                },
                addAccount,
            ) { addAccountElement, addRequest -> addAccountElement.addPiggyAccount(addRequest) }
        }

        get("/{userId?}") {
            log.debug { "Get user accounts" }
            val userId = call.parameters["userId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "User Id request param can't be null"
            )
            log.debug { "Get accounts for user = [$userId}]" }
            UserId.from(userId)
                .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid userId param") }
                .onRight {
                    getAccounts.execute(GetAccountRequest(it))
                        .onLeft { error ->
                            when (error) {
                                GetAccountError.UserNotFound -> call.respond(HttpStatusCode.NotFound)
                            }
                        }
                        .onRight { data ->
                            call.respond(HttpStatusCode.OK, data.accounts.map { dto -> dto.toRestResponse() })
                        }
                }
        }

        delete("/{accountId?}/{userId?}") {
            log.debug { "Delete account request" }
            val accountIdParam = call.parameters["accountId"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "AccountId request param can't be null"
            )
            val userIdParam = call.parameters["userId"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "UserId request param can't be null"
            )
            log.debug { "Delete account with id [$accountIdParam] for user [$userIdParam]" }
            either {
                val accountId = AccountId.from(accountIdParam).bind()
                val userId = UserId.from(userIdParam).bind()
                RemoveAccountRequestDto(userId, accountId)
            }
                .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid request params") }
                .onRight { dto ->
                    removeAccount.execute(dto)
                        .onLeft { error ->
                            when (error) {
                                RemoveAccountError.UserNotFound -> call.respond(HttpStatusCode.NotFound)
                                RemoveAccountError.AccountNotFound -> call.respond(HttpStatusCode.NotFound)
                            }
                        }
                        .onRight { call.respond(HttpStatusCode.NoContent) }
                }
        }

        put("/broker") {
            log.debug { "Update broker account request" }
            val requestBody = call.receive<UpdateBrokerAccountRestRequest>()
            log.trace { "Receive request $requestBody" }
            executeUpdateAccountRequest(
                either {
                    val accountId = AccountId.from(requestBody.accountId).bind()
                    val userId = UserId.from(requestBody.userId).bind()
                    val bankName = requestBody.bankName?.let { BankName.from(it).bind() }
                    val description = requestBody.description?.let { AccountDescription.from(it).bind() }
                    UpdateBrokerAccountRequest(userId, accountId, bankName, description)
                },
                updateAccount
            )
        }

        put("/card") {
            log.debug { "Update card account request" }
            val requestBody = call.receive<UpdateCardAccountRestRequest>()
            log.trace { "Receive request $requestBody" }
            executeUpdateAccountRequest(
                either {
                    val accountId = AccountId.from(requestBody.accountId).bind()
                    val userId = UserId.from(requestBody.userId).bind()
                    val bankName = requestBody.bankName?.let { BankName.from(it).bind() }
                    val description = requestBody.description?.let { AccountDescription.from(it).bind() }
                    UpdateCardAccountRequest(userId, accountId, bankName, description)
                },
                updateAccount
            )
        }

        put("/deposit") {
            log.debug { "Update deposit account request" }
            val requestBody = call.receive<UpdateDepositAccountRestRequest>()
            log.trace { "Receive request $requestBody" }
            executeUpdateAccountRequest(
                either {
                    val accountId = AccountId.from(requestBody.accountId).bind()
                    val userId = UserId.from(requestBody.userId).bind()
                    val bankName = requestBody.bankName?.let { BankName.from(it).bind() }
                    val description = requestBody.description?.let { AccountDescription.from(it).bind() }
                    val interest = requestBody.interest?.let { Interest.from(it).bind() }
                    val openedDate = requestBody.openedDate
                    val closedDate = requestBody.closedDate
                    UpdateDepositAccountRequest(
                        userId,
                        accountId,
                        bankName,
                        description,
                        interest,
                        openedDate,
                        closedDate
                    )
                },
                updateAccount
            )
        }

        put("/piggy") {
            log.debug { "Update piggy account request" }
            val requestBody = call.receive<UpdatePiggyAccountRestRequest>()
            log.trace { "Receive request $requestBody" }
            executeUpdateAccountRequest(
                either {
                    val accountId = AccountId.from(requestBody.accountId).bind()
                    val userId = UserId.from(requestBody.userId).bind()
                    val bankName = requestBody.bankName?.let { BankName.from(it).bind() }
                    val description = requestBody.description?.let { AccountDescription.from(it).bind() }
                    val interest = requestBody.interest?.let { Interest.from(it).bind() }
                    UpdatePiggyAccountRequest(
                        userId,
                        accountId,
                        bankName,
                        description,
                        interest
                    )
                },
                updateAccount
            )
        }
    }
}

private suspend fun <T : AddNewAccountDTO> PipelineContext<Unit, ApplicationCall>.executeAddAccountRequest(
    addAccountRequest: Either<DomainError, T>,
    addAccount: AddAccount,
    action: BiFunction<AddAccount, T, Either<AddNewAccountError, Unit>>
) {
    addAccountRequest
        .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid request body") }
        .onRight { request ->
            action.apply(addAccount, request)
                .onLeft { error ->
                    when (error) {
                        AddNewAccountError.AccountAlreadyExists -> call.respond(HttpStatusCode.BadRequest)
                        AddNewAccountError.UserNotFound -> call.respond(HttpStatusCode.NotFound)
                    }
                }
                .onRight { call.respond(HttpStatusCode.Created) }
        }
}

private suspend fun <T : UpdateAccountRequest> PipelineContext<Unit, ApplicationCall>.executeUpdateAccountRequest(
    updateAccountRequest: Either<DomainError, T>,
    updateAccount: UpdateAccount
) {
    updateAccountRequest
        .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid request body") }
        .onRight { request ->
            updateAccount.execute(request)
                .onLeft { error ->
                    when (error) {
                        UpdateAccountError.InvalidRequest -> call.respond(HttpStatusCode.BadRequest)
                        UpdateAccountError.AccountNotFound -> call.respond(HttpStatusCode.NotFound)
                        UpdateAccountError.UserNotFound -> call.respond(HttpStatusCode.NotFound)
                    }
                }
                .onRight { call.respond(HttpStatusCode.Created) }
        }
}