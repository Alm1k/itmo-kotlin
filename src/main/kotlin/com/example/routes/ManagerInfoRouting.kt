package com.example.routes

import com.example.dao.managerInfo.managerInfoService
import com.example.models.ApiError
import com.example.models.ERole
import com.example.models.ManagerInfoDTO
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.managerInfoRouting() {

    authenticate {

        authorized(rolesMap.getValue(ERole.DIRECTOR).toString(),
            rolesMap.getValue(ERole.MANAGER).toString()) {

            route("/api/managerInfo") {

                route("/{managerId}") {
                    get {
                        val id = call.parameters["managerId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                        try {
                            val managerInfo: ManagerInfoDTO? = managerInfoService.getManagerInfoByManagerId(id)
                            if (managerInfo != null) {
                                call.respond(HttpStatusCode.OK, managerInfo)
                            }
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.NotFound, message = ApiError(
                                    "USER_NOT_FOUND",
                                    "Manager with id $id was not found (error: $e)"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
