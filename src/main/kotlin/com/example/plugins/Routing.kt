package com.example.plugins

import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondRedirect("/api/users")
        }
        usersRouting()
        authRouting()
        managerInfoRouting()
        roomsRouting()
        hotelRouting()
        directorInfoRouting()
        rolesRouting()
        cleanerInfoRouting()
        requestRouting()
    }
}
