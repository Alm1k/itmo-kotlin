package com.example.routes

import com.example.dao.cleanerInfo.cleanerInfoService
import com.example.dao.cleaning.cleaningService
import com.example.models.*
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class CleanerInfo(val cleanerId: Int, val hotelId: Int)
fun Route.cleanerInfoRouting() {

    authenticate {

        authorized(rolesMap.getValue(ERole.DIRECTOR).toString(), rolesMap.getValue(ERole.MANAGER).toString()) {
            route("/api/cleanerInfo") {

                post {
                    val data = call.receive<CleanerInfo>()

                    try {
                        cleanerInfoService.addCleanerInfo(data.cleanerId, data.hotelId)
                        call.respond("Cleaner with id ${data.cleanerId} was added to hotel ${data.hotelId}")
                    } catch (e: Exception) {

                        // todo change it, I think we need to do return ApiError from service
                        call.respond(
                            HttpStatusCode.BadRequest, message = ApiError(
                                "DIRECTOR_ALREADY_EXISTS",
                                "$e: this director already exists"
                            )
                        )
                    }
                }

                authorized(
                    rolesMap.getValue(ERole.DIRECTOR).toString(),
                    rolesMap.getValue(ERole.MANAGER).toString(),
                    rolesMap.getValue(ERole.CLEANER).toString()
                ) {

                    route("/{cleanerId}") {
                        get {
                            val id =
                                call.parameters["cleanerId"]?.toIntOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")

                            try {
                                val cleanerInfo: CleanerInfoDTO? = cleanerInfoService.getCleanerInfoById(id)

                                if (cleanerInfo != null) {
                                    call.respond(HttpStatusCode.OK, cleanerInfo)
                                }
                            } catch (e: Exception) {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "",
                                        "$e: Cleaner with id $id was not found"
                                    )
                                )
                            }
                        }
                    }

                    route("/cleanings") {
                        get {
                            val id = call.parameters["cleanerId"]?.toIntOrNull()
                                ?: throw IllegalArgumentException("Invalid ID")

                            try {
                                val cleaning: List<CleaningDTO> = cleaningService.getCleaningsByCleaner(id)

                                call.respond(HttpStatusCode.OK, cleaning)
                            } catch (e: Exception) {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "",
                                        "$e"
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