package com.example.routes

import com.example.dao.cleaning.cleaningService
import com.example.models.ApiError
import com.example.models.ERole
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class CleaningRequest(val cleanerId: Int, val hotelId: Int, val roomId: Int)

fun Route.requestRouting() {

    authenticate {

        route("/api/requests") {

            route("/cleanings") {
                post {
                    val data = call.receive<CleaningRequest>()

                    try {
                        cleaningService.addCleaning(data.cleanerId, data.roomId, data.hotelId)
                        call.respond("Cleaning with cleaner ${data.cleanerId} for hotel ${data.hotelId} and room ${data.roomId}")
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.NotFound, message = ApiError(
                                "",
                                "${e.message}"
                            )
                        )
                    }
                }

                authorized(
                    rolesMap.getValue(ERole.DIRECTOR).toString(),
                    rolesMap.getValue(ERole.MANAGER).toString(),
                    rolesMap.getValue(ERole.CLEANER).toString()
                ) {

                    route("/{cleaningId}") {
                        post {
                            val id =
                                call.parameters["cleaningId"]?.toIntOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")

                            try {
                                cleaningService.completeCleaning(id)
                                call.respond("Cleaning $id is completed")
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