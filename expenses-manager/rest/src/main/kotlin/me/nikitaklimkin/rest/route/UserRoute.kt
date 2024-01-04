package me.nikitaklimkin.rest.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nikitaklimkin.rest.util.API_V1_ADD_TELEGRAM_USER
import me.nikitaklimkin.rest.util.API_V1_USER
import me.nikitaklimkin.useCase.AddNewUser
import me.nikitaklimkin.useCase.AddSimpleUserRequest
import me.nikitaklimkin.useCase.AddTelegramUserRequest
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger {}
fun Route.userRoute() {

    val addNewUser: AddNewUser by inject()

    route(API_V1_USER) {

        post {
            log.debug { "Receive add new user request" }
            val body = call.receive<AddSimpleUserRequest>()
            log.trace { "Receive add new user request with body [$body]" }
            addNewUser.executeBySimpleInfo(body)
                .onLeft {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                }
                .onRight {
                    call.respond(
                        HttpStatusCode.Created
                    )
                }
        }

        post(API_V1_ADD_TELEGRAM_USER) {
            log.debug { "Receive add new user request by telegram info" }
            val body = call.receive<AddTelegramUserRequest>()
            log.trace { "Receive add new user request by telegram info = [$body]" }
            addNewUser.executeByTelegramInfo(body)
                .onLeft {
                    call.respond(
                        HttpStatusCode.BadRequest
                    )
                }
                .onRight {
                    call.respond(
                        HttpStatusCode.Created
                    )
                }
        }

    }


}