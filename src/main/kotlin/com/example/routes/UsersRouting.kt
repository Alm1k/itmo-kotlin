package com.example.routes

import com.example.dao.user.userService
import com.example.models.ApiError
import com.example.models.ERole
import com.example.models.UserDTO
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRouting() {

    authenticate {

        authorized(
            rolesMap.getValue(ERole.DIRECTOR).toString(),
            rolesMap.getValue(ERole.MANAGER).toString()) {

            route("/api/users") {

                get {
                    call.respond(userService.getAllUsers())
                }

                route("/{userId}") {

                    get {
                        val id =
                            call.parameters["userId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                        try {
                            val user: UserDTO? = userService.getUser(id)
                            if (user != null) {
                                call.respond(HttpStatusCode.OK, user)
                            }
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.NotFound, message = ApiError(
                                    "USER_NOT_FOUND",
                                    "User  with id $id was not found"
                                )
                            )
                        }
                    }

                    delete {
                        val userId = call.parameters["userId"]?.toIntOrNull()

                        if (userId != null) {
                            val deleted = userService.deleteUser(userId)
                            if (deleted) {
                                call.respond(HttpStatusCode.OK, "User deleted")
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "USER_NOT_FOUND",
                                        "User  with id $userId was not found"
                                    )
                                )
                            }
                        } else {
                            call.respond(
                                HttpStatusCode.BadRequest, message = ApiError(
                                    "INVALID_ID",
                                    "Invalid user ID"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
