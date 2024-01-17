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

        authorized(
            rolesMap.getValue(ERole.DIRECTOR).toString(),
            rolesMap.getValue(ERole.MANAGER).toString()) {

            route("/api/managerInfo") {

                route("/{managerId}") {
                    get {
                        val id = call.parameters["managerId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                        try {
                            val managerInfo: ManagerInfoDTO = managerInfoService.getManagerInfoByManagerId(id)
                            call.respond(HttpStatusCode.OK, managerInfo)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }
            }
        }
    }
}
