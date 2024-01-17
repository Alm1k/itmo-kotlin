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

data class CleanerInfoRequest(val cleanerId: Int, val hotelId: Int)

fun Route.cleanerInfoRouting() {

    route("/api/cleanerInfo") {

        authenticate {

            authorized(
                rolesMap.getValue(ERole.DIRECTOR).toString(),
                rolesMap.getValue(ERole.MANAGER).toString()
            ) {

                post {
                    val data: CleanerInfoRequest

                    try {
                        data = call.receive<CleanerInfoRequest>()
                    } catch (e: Throwable) {
                        call.respond(HttpStatusCode.UnprocessableEntity,
                            "failed to convert request body to class CleanerInfoRequest")

                        return@post
                    }

                    try {
                        cleanerInfoService.addCleanerInfo(data.cleanerId, data.hotelId)
                        call.respond("Cleaner with id ${data.cleanerId} was added to hotel ${data.hotelId}")
                    } catch (e: ApiError) {
                        call.respond(e.code, e.message)
                    }
                }
            }
        }

        authenticate {

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
                            val cleanerInfo: CleanerInfoDTO = cleanerInfoService.getCleanerInfoByCleanerId(id)

                            call.respond(HttpStatusCode.OK, cleanerInfo)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
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
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }
            }
        }
    }
}
