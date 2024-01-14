package com.example.routes

import com.example.dao.room.roomService
import com.example.dao.user.userService
import com.example.models.*
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//todo: set managerinfo for rooms in patch for cases where new managerinfo is created

data class RoomRequest(val number: Int, val capacity: Int, val floor: Int, val price: Double,
    val isVip: Boolean, val managerInfoId: Int, val hotelId: Int)
fun Route.roomsRouting() {

    authenticate {

        authorized(
            rolesMap.getValue(ERole.DIRECTOR).toString(),
            rolesMap.getValue(ERole.MANAGER).toString()) {

            route("/api/rooms") {

                get {
                    call.respond(roomService.getAllRooms())
                }
                post {
                    val room = call.receive<RoomRequest>()
                    roomService.addRoom(
                        room.number,
                        room.capacity,
                        room.floor,
                        room.price,
                        room.isVip,
                        room.managerInfoId,
                        room.hotelId,
                    ) ?:
                    call.respond("new room created")
                }

                route("/{roomId}") {

                    get {
                        val id =
                            call.parameters["roomId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
                        try {
                            val room: RoomDTO? = roomService.getRoom(id)
                            if (room != null) {
                                call.respond(HttpStatusCode.OK, room)
                            }
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.NotFound, message = ApiError(
                                    "ROOM_NOT_FOUND",
                                    "Room with id $id was not found"
                                )
                            )
                        }
                    }

                    patch {
                        val managerInfoId = call.parameters["managerInfoId"]?.toIntOrNull()

                    }

                    delete {
                        val roomId = call.parameters["roomId"]?.toIntOrNull()

                        if (roomId != null) {
                            val deleted = userService.deleteUser(roomId)
                            if (deleted) {
                                call.respond(HttpStatusCode.OK, "Room deleted")
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "ROOM_NOT_FOUND",
                                        "Room  with id $roomId was not found"
                                    )
                                )
                            }
                        } else {
                            call.respond(
                                HttpStatusCode.BadRequest, message = ApiError(
                                    "INVALID_ID",
                                    "Invalid room ID"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
