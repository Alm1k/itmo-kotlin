package com.example.routes

import com.example.dao.cleaning.cleaningService
import com.example.dao.request.requestService
import com.example.dao.user.userService
import com.example.dao.wsConnections.wsConnectionsService
import com.example.models.ApiError
import com.example.models.ERole
import com.example.models.User
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

data class CleaningRequest(val cleanerId: Int, val hotelId: Int, val roomId: Int)
data class RequestRequest(
    val fromClientId: Int,
    val hotelId: Int,
    val roomId: Int,
    val type: String,
    val additionalInfo: String
)

data class RequestEditRequest(
    val type: String,
    val additionalInfo: String
)

data class RequestEditStatusRequest(
    val status: String,
)

fun Route.requestRouting() {
    authenticate {
        webSocket("/requests/ws") {
            send("Successfully connected.")
            val principal = call.principal<JWTPrincipal>()
            val login = principal!!.payload.getClaim("login").asString()
            val user: User
            try {
                user = userService.findUserByLogin(login)
            } catch (e: ApiError) {
                this.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, e.message))

                return@webSocket
            }

            try {
                wsConnectionsService.connect(user.id.value, this)

                incoming.consumeEach { }

                wsConnectionsService.disconnect(user.id.value)
            } catch (e: IllegalStateException) {
                this.close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, e.localizedMessage))
            }
        }
    }

    route("/api/requests") {

        post {
            val data: RequestRequest

            try {
                data = call.receive<RequestRequest>()
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    "failed to convert request body to class RequestRequest"
                )

                return@post
            }

            try {
                requestService.createRequest(
                    data.fromClientId,
                    data.hotelId,
                    data.roomId,
                    data.type,
                    data.additionalInfo
                )

                wsConnectionsService.sendMessageToAllManagersByHotel(
                    data.hotelId,
                    "New request created for hotel = ${data.hotelId} and room = ${data.roomId}, reload requests list"
                )

                call.respond(HttpStatusCode.OK, "Request successfully created")
            } catch (e: ApiError) {
                call.respond(e.code, e.message)
            }
        }

        route("/{requestId}") {
            get {
                val id =
                    call.parameters["requestId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid ID")

                try {
                    call.respond(HttpStatusCode.OK, requestService.getRequestById(id))
                } catch (e: ApiError) {
                    call.respond(e.code, e.message)
                }
            }

            post {
                val id =
                    call.parameters["requestId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid ID")
                val data: RequestEditRequest

                try {
                    data = call.receive<RequestEditRequest>()
                } catch (e: Throwable) {
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        "failed to convert request body to class RequestEditRequest"
                    )

                    return@post
                }

                try {
                    requestService.updateRequest(
                        id,
                        data.type,
                        data.additionalInfo
                    )

                    val hotelId = requestService.getRequestById(id).hotelId

                    wsConnectionsService.sendMessageToAllManagersByHotel(
                        hotelId,
                        "Request with id $id is updated, reload requests list"
                    )

                    call.respond(HttpStatusCode.OK, "Request successfully updated")
                } catch (e: ApiError) {
                    call.respond(e.code, e.message)
                }
            }

            authenticate {

                authorized(
                    rolesMap.getValue(ERole.DIRECTOR).toString(),
                    rolesMap.getValue(ERole.MANAGER).toString()
                ) {

                    route("/status") {
                        post {
                            val id =
                                call.parameters["requestId"]?.toIntOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")
                            val data: RequestEditStatusRequest

                            try {
                                data = call.receive<RequestEditStatusRequest>()
                            } catch (e: Throwable) {
                                call.respond(
                                    HttpStatusCode.UnprocessableEntity,
                                    "failed to convert request body to class RequestEditStatusRequest"
                                )

                                return@post
                            }


                            try {
                                requestService.updateRequestStatus(
                                    id,
                                    data.status
                                )

                                val hotelId = requestService.getRequestById(id).hotelId

                                wsConnectionsService.sendMessageToAllManagersByHotel(
                                    hotelId,
                                    "Request status with id $id is updated, reload requests list"
                                )

                                call.respond(HttpStatusCode.OK, "Request successfully updated")
                            } catch (e: ApiError) {
                                call.respond(e.code, e.message)
                            }
                        }
                    }
                }
            }
        }

        route("/cleanings") {

            authenticate {

                post {
                    val data: CleaningRequest

                    try {
                        data = call.receive<CleaningRequest>()
                    } catch (e: Throwable) {
                        call.respond(
                            HttpStatusCode.UnprocessableEntity,
                            "failed to convert request body to class CleaningRequest"
                        )

                        return@post
                    }

                    try {
                        cleaningService.createCleaning(data.cleanerId, data.roomId, data.hotelId)

                        if (wsConnectionsService.checkConnectionExist(data.cleanerId)) {
                            wsConnectionsService.sendMessage(
                                data.cleanerId,
                                "New cleaning created for room = ${data.roomId}, reload cleanings list"
                            )
                        }

                        call.respond(HttpStatusCode.OK, "Cleaning successfully created")
                    } catch (e: ApiError) {
                        call.respond(e.code, e.message)
                    }
                }
            }

            authenticate {

                authorized(
                    rolesMap.getValue(ERole.DIRECTOR).toString(),
                    rolesMap.getValue(ERole.MANAGER).toString(),
                    rolesMap.getValue(ERole.CLEANER).toString()
                ) {
                    route("/{cleaningId}") {
                        get {
                            val id =
                                call.parameters["cleaningId"]?.toIntOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")

                            try {
                                val cleaning = cleaningService.getCleaningsById(id)

                                call.respond(HttpStatusCode.OK, cleaning)
                            } catch (e: ApiError) {
                                call.respond(e.code, e.message)
                            }
                        }
                    }

                    route("/complete/{cleaningId}") {
                        post {
                            val id =
                                call.parameters["cleaningId"]?.toIntOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")

                            try {
                                cleaningService.completeCleaning(id)

                                val cleanerId = cleaningService.getCleaningsById(id).cleanerId

                                if (wsConnectionsService.checkConnectionExist(cleanerId)) {
                                    wsConnectionsService.sendMessage(
                                        cleanerId,
                                        "Cleaning $id is completed, reload cleanings list"
                                    )
                                }

                                call.respond(HttpStatusCode.OK, "Cleaning $id is completed")
                            } catch (e: ApiError) {
                                call.respond(e.code, e.message)
                            }
                        }
                    }
                }
            }
        }
    }
}
