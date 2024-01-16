package com.example.routes

import com.example.dao.directorInfo.directorInfoService
import com.example.models.ApiError
import com.example.models.DirectorInfoDTO
import com.example.models.ERole
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class DirectorInfo(val directorId: Int)
fun Route.directorInfoRouting() {

    authenticate {

        authorized(
            rolesMap.getValue(ERole.DIRECTOR).toString()
        ) {

            route("/api/directorInfo") {

                post {
                    val data = call.receive<DirectorInfo>()

                    try {
                        directorInfoService.addDirectorInfo(data.directorId)
                        call.respond("Director with id ${data.directorId} created")
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

                route("/{directorId}") {
                    get {
                        val id =
                            call.parameters["directorId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")

                        try {
                            val directorInfo: DirectorInfoDTO? = directorInfoService.getDirectorInfoByDirectorId(id)

                            if (directorInfo != null) {
                                call.respond(HttpStatusCode.OK, directorInfo)
                            }
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.NotFound, message = ApiError(
                                    "DIRECTOR_NOT_FOUND",
                                    "$e: Director with id $id was not found"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
