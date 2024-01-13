package com.example.plugins

import com.example.routes.authRouting
import com.example.routes.managerInfoRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.usersRouting
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondRedirect("/api/users")
        }
        usersRouting()
        authRouting()
        managerInfoRouting()
    }
}
