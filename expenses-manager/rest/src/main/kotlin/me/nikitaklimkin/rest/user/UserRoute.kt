package me.nikitaklimkin.rest.user

import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.nikitaklimkin.domain.user.UserName
import me.nikitaklimkin.rest.user.dto.AddUserRestRequest
import me.nikitaklimkin.useCase.user.AddNewUser
import me.nikitaklimkin.useCase.user.AddSimpleUserRequest
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger {}
fun Route.userRoute() {

    val addNewUser: AddNewUser by inject()

    route(API_V1_USER) {

        post {
            log.debug { "Receive add new user request" }
            val body = call.receive<AddUserRestRequest>()
            log.trace { "Receive add new user request with body [$body]" }
            either {
                val userName = UserName.from(body.userName).bind()
                AddSimpleUserRequest(userName)
            }
                .onLeft { call.respond(HttpStatusCode.BadRequest) }
                .onRight {
                    addNewUser.executeBySimpleInfo(it)
                        .onLeft { call.respond(HttpStatusCode.InternalServerError) }
                        .onRight { call.respond(HttpStatusCode.Created) }
                }

        }
    }
}