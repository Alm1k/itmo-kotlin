package com.example.routes

import com.example.dao.room.roomService
import com.example.models.*
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.reflect.full.createInstance

data class RoomUpdateRequest(val managerId: Int, val price: Double) {
    constructor() : this(0, 0.00)
}
data class RoomRequest(val number: Int, val capacity: Int, val floor: Int, val price: Double,
    val isVip: Boolean, val managerId: Int, val hotelId: Int) {
    constructor() : this(0,0,0,0.00,false,0,0)
}

fun Route.roomsRouting() {

    route("/api/rooms") {

        authenticate {

            authorized(
                rolesMap.getValue(ERole.DIRECTOR).toString(),
                rolesMap.getValue(ERole.MANAGER).toString()
            ) {


                get {
                    call.respond(roomService.getAllRooms())
                }

                post {
                    var room = RoomRequest::class.createInstance()
                    try {
                        room = call.receive<RoomRequest>()
                    } catch (e: Throwable) {
                        call.respond(HttpStatusCode.UnprocessableEntity,
                            "failed to convert request body to class RoomRequest")
                    }
                    try {
                        roomService.addRoom(
                            room.number,
                            room.capacity,
                            room.floor,
                            room.price,
                            room.isVip,
                            room.managerId,
                            room.hotelId,
                        )
                        call.respond("new room created")
                    } catch (e: ApiError) {
                        call.respond(e.code, e.message)
                    }
                }

                route("/{roomId}") {

                    delete {
                        val roomId = call.parameters["roomId"]?.toIntOrNull()

                        try {
                            roomService.deleteRoom(roomId)
                            call.respond(HttpStatusCode.OK, "Room deleted")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }

                    patch {
                        val roomId = call.parameters["roomId"]?.toIntOrNull()

                        var updateInfo = RoomUpdateRequest::class.createInstance()
                        try {
                            updateInfo = call.receive<RoomUpdateRequest>()
                        } catch (e: Throwable) {
                            call.respond(HttpStatusCode.UnprocessableEntity,
                                "failed to convert request body to class RoomUpdateRequest")
                        }
                        try {
                            roomService.updateRoom(roomId, updateInfo.managerId, updateInfo.price)
                            call.respond(HttpStatusCode.OK, "Room info updated")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
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

                route("/{roomId}") {

                    get {
                        val id = call.parameters["roomId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                        try {
                            val room = roomService.getRoom(id)
                            call.respond(HttpStatusCode.OK, room)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }
            }
        }
    }
}
