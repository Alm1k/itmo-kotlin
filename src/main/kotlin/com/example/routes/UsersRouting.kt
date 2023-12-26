package com.example.routes

import com.example.dao.user.UserServiceImpl
import com.example.models.ApiError
import com.example.models.User
import com.example.dao.user.service
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.file.attribute.UserPrincipalLookupService

fun Route.usersRouting() {

    route("/api/users") {
        get {
            call.respond(service.getAllUsers())
        }

        post {
            val user = call.receive<User>()
            val newUser = service.addNewUser(user.name, user.surname,
                user.password, user.login,)
            call.respond(HttpStatusCode.OK)
            return@post

        }

        route("/{userId}") {

            get {
                val id = call.parameters["userId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                try {
//                    val user: User? = Storage.users[id]
//                    if (user != null) {
//                        call.respond(HttpStatusCode.OK, user)
//                    } else {
//                        call.respond(HttpStatusCode.NotFound, message = ApiError("USER_NOT_FOUND",
//                            "User  with id $id was not found"))
//                    }
                } catch (e: Exception) {
                    // Handle exceptions or errors accordingly
                }
            }

            delete {
                val userId = call.parameters["id"]?.toIntOrNull()

//                if (userId != null) {
//                    val deleted = deleteUserById(userId)
//                    if (deleted) {
//                        call.respond(HttpStatusCode.OK, "User deleted")
//                    } else {
//                        call.respond(HttpStatusCode.NotFound, message = ApiError("USER_NOT_FOUND",
//                            "User  with id $userId was not found"))
//                    }
//                } else {
//                    call.respond(HttpStatusCode.BadRequest, message = ApiError("INVALID_ID",
//                        "Invalid user ID"))
//                }
            }
        }
    }
}
