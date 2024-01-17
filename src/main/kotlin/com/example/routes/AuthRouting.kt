package com.example.routes

import com.example.dao.cleanerInfo.cleanerInfoService
import com.example.dao.directorInfo.directorInfoService
import com.example.dao.managerInfo.managerInfoService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.dao.user.userService
import com.example.models.ApiError
import com.example.models.ERole
import com.example.models.rolesMap
import com.example.utils.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.delay
import kotlin.reflect.full.createInstance

interface IRegisterRequest {
    val name: String
    val surname: String
    val login: String
    val password: String
}
data class RegisterRequest(override var name: String, override var surname: String,
                           override var login: String, override var password: String): IRegisterRequest {
    constructor() : this("", "", "", "")
}
data class CleanerRegisterRequest(val hotelId: Int, override val name: String,
                                  override val surname: String, override val login: String,
                                  override val password: String) : IRegisterRequest {
    constructor() : this(1, "", "", "", "")
}

data class LoginRequest(val login: String, val password: String) {
    constructor(): this("", "")
}

fun Route.authRouting() {

    route("/register") {

        route("/client") {
            post {
                var creds = RegisterRequest::class.createInstance()
                try {
                    creds = call.receive<RegisterRequest>()
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.UnprocessableEntity,
                        "failed to convert request body to class RegisterRequest")
                }
                try {
                    userService.addUser(
                        creds.name,
                        creds.surname,
                        ERole.USER.databaseId,
                        creds.login,
                        BcryptHasher.hashPassword(creds.password)
                    )
                    ?: call.respond("new client registered")
                } catch (e: ApiError) {
                    call.respond(e.code, e.message)
                }
            }
        }

        authenticate {

            authorized(rolesMap.getValue(ERole.DIRECTOR).toString()) {

                route("/manager") {
                    post {
                        var creds = RegisterRequest::class.createInstance()
                        try {
                            creds = call.receive<RegisterRequest>()
                        } catch (e: Throwable) {
                            call.respond(HttpStatusCode.UnprocessableEntity,
                                "failed to convert request body to class RegisterRequest")
                        }
                        try {
                            val user = userService.addUser(
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
                        } catch (e: ApiError)  {
                            call.respond(e.code, e.message)
                        }
                    }
                }

                route("/director") {
                    post {
                        var creds = RegisterRequest::class.createInstance()
                        try {
                            creds = call.receive<RegisterRequest>()
                        } catch (e: Throwable) {
                            call.respond(HttpStatusCode.UnprocessableEntity,
                                "failed to convert request body to class RegisterRequest")
                        }
                        try {
                            val user = userService.addUser(
                                creds.name,
                                creds.surname,
                                ERole.DIRECTOR.databaseId,
                                creds.login,
                                BcryptHasher.hashPassword(creds.password)
                            )
                            delay(0)
                            if (user != null) {
                                directorInfoService.addDirectorInfo(user.id)
                            }

                            call.respond("new director registered")
                        } catch(e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }

                route("/cleaner") {
                    post {
                        var creds = CleanerRegisterRequest::class.createInstance()
                        try {
                            creds = call.receive<CleanerRegisterRequest>()
                        } catch (e: Throwable) {
                            call.respond(HttpStatusCode.UnprocessableEntity,
                                "failed to convert request body to class CleanerRegisterRequest")
                        }
                        try {
                            val user = userService.addUser(
                                creds.name,
                                creds.surname,
                                ERole.CLEANER.databaseId,
                                creds.login,
                                BcryptHasher.hashPassword(creds.password)
                            )
                            delay(0)
                            if (user != null) {
                                cleanerInfoService.addCleanerInfo(user.id, creds.hotelId)
                            }

                            call.respond("new cleaner registered")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }
            }
        }
    }

    route("/login") {

        post("") {
            var loginRequest = LoginRequest::class.createInstance()
            try {
                loginRequest = call.receive<LoginRequest>()
            } catch (e: Throwable) {
                call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    "failed to convert request body to class LoginRequest"
                )
            }
            try {
                userService.findUserByCredentials(loginRequest)?.let {
                    token -> call.respond(Token(JwtConfig.createJwtToken(token))) }
            } catch (e: ApiError) {
                call.respond(e.code, e.message)
            }
        }
    }
}
