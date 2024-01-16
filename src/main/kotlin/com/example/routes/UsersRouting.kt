package com.example.routes

import com.example.dao.user.userService
import com.example.dao.hotelRating.hotelRatingService
import com.example.models.ApiError
import com.example.models.HotelRatingDTO
import com.example.models.ERole
import com.example.models.UserDTO
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class userUpdateRequest(val bDay: String, val email: String)

fun Route.usersRouting() {

    route("/api/users") {

    authenticate {

        authorized(
            rolesMap.getValue(ERole.DIRECTOR).toString(),
            rolesMap.getValue(ERole.MANAGER).toString()
        ) {

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

                route("/ratings") {
                    get {
                        val id =
                            call.parameters["userId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")

                        try {
                            val ratings: List<HotelRatingDTO>? = hotelRatingService.getUserRatings(id)
                            if (ratings != null) {
                                call.respond(HttpStatusCode.OK, ratings)
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
                }
            }
        }
    }

    authenticate {

        authorized(
            rolesMap.getValue(ERole.DIRECTOR).toString(),
            rolesMap.getValue(ERole.MANAGER).toString(),
            rolesMap.getValue(ERole.USER).toString()
        ) {

            route("/{userId}") {

                patch {
                    val userId = call.parameters["userId"]?.toIntOrNull()

                    if (userId != null) {
                        val additionalUserInfo = call.receive<userUpdateRequest>()
                        val updated = userService.updateUser(userId, additionalUserInfo.bDay, additionalUserInfo.email)
                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "User info updated")
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
