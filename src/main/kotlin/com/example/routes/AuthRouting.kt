package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.dao.user.service
import com.example.utils.BcryptHasher
import com.example.utils.JwtConfig
import com.example.utils.Token
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*

data class RegisterRequest(val name: String, val surname: String, val roleId: Int, val login: String, val password: String)
data class LoginRequest(val login: String, val password: String)
fun Route.authRouting() {

    route("/register") {

        post("/client") {
            val creds = call.receive<RegisterRequest>()
            service.addNewUser(creds.name, creds.surname, creds.roleId, creds.login, BcryptHasher.hashPassword(creds.password))
            call.respond("new client registered!")
        }

        post("/manager") {
            val creds = call.receive<RegisterRequest>()
            service.addNewUser(creds.name, creds.surname, creds.roleId, creds.login, BcryptHasher.hashPassword(creds.password))
            call.respond("new manager registered!")
        }
    }

    route("/login") {
        post("") {
            service.findUserByCredentials(call.receive<LoginRequest>())?.let { token ->
                call.respond(Token(JwtConfig.createJwtToken(token)))
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
