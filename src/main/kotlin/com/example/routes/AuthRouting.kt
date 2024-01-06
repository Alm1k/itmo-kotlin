package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import com.example.dao.user.service
import com.example.utils.BcryptHasher
import com.example.utils.JwtConfig
import com.example.utils.Token
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*

data class RegisterRequest(val name: String, val surname: String, val roleId: Int, val login: String, val password: String)
fun Route.authRouting() {

    route("") {

        post("/register") {

            val creds = call.receive<RegisterRequest>()
            service.addNewUser(creds.name, creds.surname, creds.roleId, creds.login, BcryptHasher.hashPassword(creds.password))
            call.respond("new user registered!")
        }

        post("/login") {
            service.findUserByCredentials(call.receive<UserPasswordCredential>())?.let {
                    token -> call.respond(Token(JwtConfig.createJwtToken(token)))
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
