package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.dao.user.userService
import com.example.models.ERole
import com.example.utils.BcryptHasher
import com.example.utils.JwtConfig
import com.example.utils.Token
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*

data class RegisterRequest(val name: String, val surname: String, val login: String, val password: String)
data class LoginRequest(val login: String, val password: String)
fun Route.authRouting() {

    route("/register") {

        route("/client") {
            post {
                val creds = call.receive<RegisterRequest>()
                userService.addNewUser(
                    creds.name,
                    creds.surname,
                    ERole.USER.databaseId,
                    creds.login,
                    BcryptHasher.hashPassword(creds.password)
                ) ?:
                call.respond("new client registered")
            }
        }

        route("/manager") {
            post {
                val creds = call.receive<RegisterRequest>()
                userService.addNewUser(
                    creds.name,
                    creds.surname,
                    ERole.MANAGER.databaseId,
                    creds.login,
                    BcryptHasher.hashPassword(creds.password)
                )
                call.respond("new manager registered")
            }
        }
    }
    route("/login") {
        post("") {
            userService.findUserByCredentials(call.receive<LoginRequest>())?.let { token ->
                call.respond(Token(JwtConfig.createJwtToken(token)))
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
