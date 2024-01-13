package com.example.routes

import com.example.dao.managerInfo.managerInfoService
import com.example.models.ApiError
import com.example.models.ManagerInfoDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class managerInfo(val managerId: Int)
fun Route.managerInfoRouting() {

    authenticate {

        route("/api/managerInfo") {

            post {
                val creds = call.receive<managerInfo>()
                val managerId = creds.managerId
                managerInfoService.addManagerInfo(
                    creds.managerId
                ) ?: call.respond("manager info for manager with id $managerId created")
            }

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
                                "Manager $e with id $id was not found"
                            )
                        )
                    }
                }
            }
        }
    }
}
