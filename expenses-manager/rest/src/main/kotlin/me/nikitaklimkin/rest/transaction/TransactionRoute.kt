package me.nikitaklimkin.rest.transaction

import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nikitaklimkin.domain.MoneyAmount
import me.nikitaklimkin.domain.account.AccountId
import me.nikitaklimkin.domain.transaction.Category
import me.nikitaklimkin.domain.transaction.Direction
import me.nikitaklimkin.domain.transaction.TransactionId
import me.nikitaklimkin.domain.transaction.TransactionName
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.rest.transaction.dto.AddTransactionRestRequest
import me.nikitaklimkin.rest.transaction.dto.UpdateTransactionRestRequest
import me.nikitaklimkin.rest.transaction.dto.toDetails
import me.nikitaklimkin.useCase.transaction.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger { }

fun Route.transactionRoute() {

    val addNewTransaction: AddNewTransaction by inject()
    val updateTransaction: UpdateTransaction by inject()
    val removeTransaction: RemoveTransaction by inject()
    val getTransactions: GetTransactions by inject()

    route(API_V1_TRANSACTIONS) {

        post {
            log.debug { "Receive request to add new transaction" }
            val body = call.receive<AddTransactionRestRequest>()
            log.trace { "Receive body = [$body]" }
            either {
                val name = TransactionName.from(body.name).bind()
                val amount = MoneyAmount.from(java.math.BigDecimal(body.amount))
                val type = Category.from(body.type).bind()
                val direction = Direction.from(body.direction).bind()
                AddNewTransactionDTO(
                    AccountId.init(),
                    name,
                    amount,
                    type,
                    direction,
                    body.description,
                )
            }
                .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid request body") }
                .onRight { dto ->
                    addNewTransaction.execute(dto)
                        .onLeft { error ->
                            when (error) {
                                AddNewTransactionError.AccountNotFound -> call.respond(
                                    HttpStatusCode.NotFound,
                                    "There is no such account"
                                )
                            }
                        }
                        .onRight { call.respond(HttpStatusCode.Created) }
                }
        }

        put {
            log.debug { "Receive request to update transaction" }
            val body = call.receive<UpdateTransactionRestRequest>()
            log.trace { "Receive body = [$body]" }
            either {
                val transactionId = TransactionId.from(body.id).bind()
                val name = body.name?.let { TransactionName.from(it) }?.bind()
                val amount = body.amount?.let { MoneyAmount.from(java.math.BigDecimal(it)) }
                val type = body.type?.let { Category.from(it) }?.bind()
                val direction = body.direction?.let { Direction.from(it) }?.bind()
                val userId = UserId.from(body.userId).bind()
                UpdateTransactionDTO(
                    transactionId,
                    userId,
                    name,
                    amount,
                    type,
                    direction,
                    body.description,
                    body.created
                )
            }
                .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid request body") }
                .onRight { dto ->
                    updateTransaction.execute(dto)
                        .onLeft { error ->
                            when (error) {
                                UpdateTransactionError.TransactionNotFound -> call.respond(
                                    HttpStatusCode.NotFound,
                                    "There is no requested transaction"
                                )
                            }
                        }
                        .onRight { call.respond(HttpStatusCode.Created) }
                }
        }

        delete("/{id?}/user/{userId?}") {
            log.debug { "Receive request to delete transaction" }
            val idParam = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "There is no id path parameter"
            )
            val userIdParam = call.parameters["userId"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                "There is no userId path parameter"
            )
            log.debug { "Delete transaction with id = [$idParam]" }
            either {
                val transactionId = TransactionId.from(idParam).bind()
                val userId = UserId.from(userIdParam).bind()
                DeleteTransactionDTO(userId, transactionId)
            }
                .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid request params") }
                .onRight { dto ->
                    removeTransaction.execute(dto)
                        .onLeft { error ->
                            when (error) {
                                RemoveTransactionError.TransactionNotFound -> call.respond(
                                    HttpStatusCode.NotFound,
                                    "There is no requested transaction"
                                )
                            }
                        }
                        .onRight { call.respond(HttpStatusCode.NoContent) }
                }
        }

        get("/account/{accountId?}/user/{userId?}") {
            log.debug { "Receive request to get transaction" }
            val accountIdParam = call.parameters["accountId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "There is no accountId path parameter"
            )
            val userIdParam = call.parameters["userId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "There is no userId path parameter"
            )
            log.debug { "Get transactions for user = [$userIdParam] and accounts = [$accountIdParam]" }
            either {
                val accountId = AccountId.from(accountIdParam).bind()
                val userId = UserId.from(userIdParam).bind()
                GetTransactionsDTO(userId, accountId)
            }
                .onLeft { call.respond(HttpStatusCode.BadRequest, "Invalid request params") }
                .onRight { dto ->
                    getTransactions.execute(dto)
                        .onLeft { error ->
                            when (error) {
                                GetTransactionsError.TransactionsNotFound -> call.respond(
                                    HttpStatusCode.NotFound,
                                    "There is no requested data"
                                )
                            }
                        }
                        .map { transactions -> transactions.map { it.toDetails() } }
                        .onRight { call.respond(HttpStatusCode.OK, it) }
                }
        }
    }
}