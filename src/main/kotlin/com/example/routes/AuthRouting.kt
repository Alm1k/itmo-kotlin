package com.example.routes

import com.example.dao.cleanerInfo.cleanerInfoService
import com.example.dao.directorInfo.directorInfoService
import com.example.dao.managerInfo.managerInfoService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.dao.user.userService
import com.example.models.ERole
import com.example.models.rolesMap
import com.example.utils.BcryptHasher
import com.example.utils.JwtConfig
import com.example.utils.Token
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.delay

interface IRegisterRequest {
    val name: String
    val surname: String
    val login: String
    val password: String
}
data class RegisterRequest(override val name: String, override val surname: String, override val login: String, override val password: String): IRegisterRequest

data class CleanerRegisterRequest(val hotelId: Int, override val name: String, override val surname: String, override val login: String, override val password: String) : IRegisterRequest
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

        authorized(rolesMap.getValue(ERole.DIRECTOR).toString()) {

            route("/manager") {
                post {
                    val creds = call.receive<RegisterRequest>()
                    val user = userService.addNewUser(
                        creds.name,
                        creds.surname,
                        ERole.MANAGER.databaseId,
                        creds.login,
                        BcryptHasher.hashPassword(creds.password)
                    )
                    delay(0)
                    if (user != null) {
                        managerInfoService.addManagerInfo(user.id)
                    }
                    call.respond("new manager registered, managerInfo created")
                }
            }

            route("/director") {
                post {
                    val creds = call.receive<RegisterRequest>()
                    val user = userService.addNewUser(
                        creds.name,
                        creds.surname,
                        ERole.DIRECTOR.databaseId,
                        creds.login,
                        BcryptHasher.hashPassword(creds.password)
                    )

                    if (user != null) {
                        directorInfoService.addDirectorInfo(user.id)
                    }

                    call.respond("new director registered")
                }
            }

            route("/cleaner") {
                post {
                    val creds = call.receive<CleanerRegisterRequest>()
                    val user = userService.addNewUser(
                        creds.name,
                        creds.surname,
                        ERole.CLEANER.databaseId,
                        creds.login,
                        BcryptHasher.hashPassword(creds.password)
                    )

                    if (user != null) {
                        cleanerInfoService.addCleanerInfo(user.id, creds.hotelId)
                    }

                    call.respond("new cleaner registered")
                }
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
