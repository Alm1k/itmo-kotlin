package com.example.routes

import com.example.dao.cleaning.cleaningService
import com.example.dao.hotel.hotelService
import com.example.dao.request.requestService
import com.example.dao.user.userService
import com.example.dao.wsConnections.wsConnectionsService
import com.example.models.ApiError
import com.example.models.ERole
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
            val user = userService.findUserByLogin(login)

            if (user == null) {
                this.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "User does not exist"))
                error("Provided user does not exist")
            }

            wsConnectionsService.connect(user.id.value, this)

            incoming.consumeEach { }

            wsConnectionsService.disconnect(user.id.value)
        }
    }

    route("/api/requests") {

        post {
            val data = call.receive<RequestRequest>()

            try {
                requestService.createRequest(
                    data.fromClientId,
                    data.hotelId,
                    data.roomId,
                    data.type,
                    data.additionalInfo
                )

                hotelService.getAllHotelManagers(data.hotelId).map {
                    if (wsConnectionsService.checkConnectionExist(it.manager.id)) {
                        wsConnectionsService.sendMessage(
                            it.manager.id,
                            "New request created for hotel = ${data.hotelId} and room = ${data.roomId}, reload requests list"
                        )
                    }
                }

                call.respond(HttpStatusCode.OK, "Request successfully created")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.NotFound, message = ApiError(
                        "",
                        "${e.message}"
                    )
                )
            }
        }

        route("/{requestId}") {
            get {
                val id =
                    call.parameters["requestId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid ID")

                try {
                    call.respond(HttpStatusCode.OK, requestService.getRequestById(id))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.NotFound, message = ApiError(
                            "",
                            "${e.message}"
                        )
                    )
                }
            }

            post {
                val id =
                    call.parameters["requestId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid ID")
                val data = call.receive<RequestEditRequest>()

                try {
                    requestService.updateRequest(
                        id,
                        data.type,
                        data.additionalInfo
                    )

                    // todo maybe add websocket message?

                    call.respond(HttpStatusCode.OK, "Request successfully updated")
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.NotFound, message = ApiError(
                            "",
                            "${e.message}"
                        )
                    )
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
                            val data = call.receive<RequestEditStatusRequest>()

                            try {
                                requestService.updateRequestStatus(
                                    id,
                                    data.status
                                )

                                // todo maybe add websocket message?

                                call.respond(HttpStatusCode.OK, "Request successfully updated")
                            } catch (e: Exception) {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "",
                                        "${e.message}"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        route("/cleanings") {

            authenticate {

                post {
                    val data = call.receive<CleaningRequest>()

                    try {
                        cleaningService.createCleaning(data.cleanerId, data.roomId, data.hotelId)

                        if (wsConnectionsService.checkConnectionExist(data.cleanerId)) {
                            wsConnectionsService.sendMessage(
                                data.cleanerId,
                                "New cleaning created for room = ${data.roomId}, reload cleanings list"
                            )
                        }

                        call.respond(HttpStatusCode.OK, "Cleaning successfully created")
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.NotFound, message = ApiError(
                                "",
                                "${e.message}"
                            )
                        )
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

                                if (cleaning != null) {
                                    call.respond(HttpStatusCode.OK, cleaning)
                                } else {
                                    error("Cleaning with $id not found")
                                }
                            } catch (e: Exception) {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "",
                                        "${e.message}"
                                    )
                                )
                            }
                        }
                    }

                    route("/complete/{cleaningId}") {
                        post {
                            val id =
                                call.parameters["cleaningId"]?.toIntOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")

                            // todo maybe add websocket message?

                            try {
                                cleaningService.completeCleaning(id)
                                call.respond(HttpStatusCode.OK, "Cleaning $id is completed")
                            } catch (e: Exception) {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "",
                                        "${e.message}"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
