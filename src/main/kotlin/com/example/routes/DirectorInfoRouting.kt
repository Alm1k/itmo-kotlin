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

data class DirectorInfoRequest(val directorId: Int)

fun Route.directorInfoRouting() {

    authenticate {

        authorized(
            rolesMap.getValue(ERole.DIRECTOR).toString()
        ) {

            route("/api/directorInfo") {

                post {
                    val data: DirectorInfoRequest

                    try {
                        data = call.receive<DirectorInfoRequest>()
                    } catch (e: Throwable) {
                        call.respond(HttpStatusCode.UnprocessableEntity,
                            "failed to convert request body to class DirectorInfoRequest")

                        return@post
                    }

                    try {
                        directorInfoService.addDirectorInfo(data.directorId)
                        call.respond("Director with id ${data.directorId} created")
                    } catch (e: ApiError) {
                        call.respond(e.code, e.message)
                    }
                }

                route("/{directorId}") {
                    get {
                        val id =
                            call.parameters["directorId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")

                        try {
                            val directorInfo: DirectorInfoDTO = directorInfoService.getDirectorInfoByDirectorId(id)

                            call.respond(HttpStatusCode.OK, directorInfo)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }
            }
        }
    }
}
