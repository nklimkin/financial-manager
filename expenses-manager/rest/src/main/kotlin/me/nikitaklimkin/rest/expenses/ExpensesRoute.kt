package me.nikitaklimkin.rest.expenses

import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nikitaklimkin.domain.expenses.Amount
import me.nikitaklimkin.domain.expenses.ExpensesId
import me.nikitaklimkin.domain.expenses.ExpensesName
import me.nikitaklimkin.domain.expenses.ExpensesType
import me.nikitaklimkin.domain.user.UserId
import me.nikitaklimkin.rest.expenses.dto.AddExpensesRestRequest
import me.nikitaklimkin.rest.expenses.dto.UpdateExpensesRestRequest
import me.nikitaklimkin.useCase.expenses.*
import mu.KotlinLogging
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger { }

fun Route.expensesRoute() {

    val addNewExpenses: AddNewExpenses by inject()
    val updateExpenses: UpdateExpenses by inject()
    val deleteExpenses: DeleteExpenses by inject()
    val getExpenses: GetExpenses by inject()

    route(API_V1_EXPENSES) {

        post {
            log.debug { "Receive request to add new expenses" }
            val body = call.receive<AddExpensesRestRequest>()
            log.trace { "Receive body = [$body]" }
            either {
                val name = ExpensesName.from(body.name).bind()
                val amount = Amount.from(body.amount).bind()
                val type = ExpensesType.from(body.type).bind()
                val userId = UserId.from(body.userId).bind()
                addNewExpenses.execute(
                    AddNewExpensesDto(
                        name,
                        amount,
                        type,
                        body.description,
                        body.created,
                        userId
                    )
                ).bind()
            }
                .onLeft { call.respond(HttpStatusCode.InternalServerError) }
                .onRight { call.respond(HttpStatusCode.Created) }
        }

        put {
            log.debug { "Receive request to update expenses" }
            val body = call.receive<UpdateExpensesRestRequest>()
            log.trace { "Receive body = [$body]" }
            either {
                val expensesId = ExpensesId.from(body.id).bind()
                val name = body.name?.let { ExpensesName.from(it) }?.bind()
                val amount = body.amount?.let { Amount.from(it) }?.bind()
                val type = body.type?.let { ExpensesType.from(it) }?.bind()
                val userId = UserId.from(body.userId).bind()
                updateExpenses.execute(
                    UpdateExpensesDto(
                        expensesId,
                        name,
                        amount,
                        type,
                        body.description,
                        body.created,
                        userId
                    )
                ).bind()
            }
                .onLeft { call.respond(HttpStatusCode.InternalServerError) }
                .onRight { call.respond(HttpStatusCode.Created) }
        }
    }

}