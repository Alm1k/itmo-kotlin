package com.example.routes

import com.example.dao.user.userService
import com.example.models.ApiError
import com.example.models.*
import com.example.dao.hotelRating.hotelRatingService
import com.example.dao.request.requestService
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.reflect.full.createInstance

data class UserUpdateRequest(val bDay: String, val email: String) {
    constructor() : this("", "")
}


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
                        val id = call.parameters["userId"]?.toIntOrNull()
                        try {
                            val user = userService.getUser(id)
                            call.respond(HttpStatusCode.OK, user)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }

                    delete {
                        val userId = call.parameters["userId"]?.toIntOrNull()
                        try {
                            userService.deleteUser(userId)
                            call.respond(HttpStatusCode.OK, "user deleted")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
//
//                route("/ratings") {
//                    get {
//                        val id =
//                            call.parameters["userId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
//
//                        try {
//                            val ratings: List<HotelRatingDTO>? = hotelRatingService.getUserRatings(id)
//                            if (ratings != null) {
//                                call.respond(HttpStatusCode.OK, ratings)
//                            }
//                        } catch (e: Exception) {
//                            call.respond(
//                                HttpStatusCode.NotFound, message = ApiError(
//                                    "USER_NOT_FOUND",
//                                    "User  with id $id was not found"
//                                )
//                            )
//                        }
//                    }
//                }
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
                        var additionalUserInfo = UserUpdateRequest::class.createInstance()
                        try {
                            additionalUserInfo = call.receive<UserUpdateRequest>()
                        } catch (e: Throwable) {
                            call.respond(HttpStatusCode.UnprocessableEntity,
                                "failed to convert request body to class UserUpdateRequest")
                        }
                        try {
                            userService.updateUser(userId, additionalUserInfo.bDay, additionalUserInfo.email)
                            call.respond(HttpStatusCode.OK, "user info updated")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }
            }
        }
    }
}
