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

data class RoomUpdateRequest(val managerId: Int, val price: Double)
data class RoomRequest(val number: Int, val capacity: Int, val floor: Int, val price: Double,
    val isVip: Boolean, val managerId: Int, val hotelId: Int)

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
                        room.managerId,
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

                    delete {
                        val roomId = call.parameters["roomId"]?.toIntOrNull()

                        if (roomId != null) {
                            val deleted = roomService.deleteRoom(roomId)
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

                    patch {
                        val roomId = call.parameters["roomId"]?.toIntOrNull()

                        if (roomId != null) {
                            val updateInfo = call.receive<RoomUpdateRequest>()
                            val updated = roomService.updateRoom(roomId, updateInfo.managerId, updateInfo.price)
                            if (updated > 0) {
                                call.respond(HttpStatusCode.OK, "Room info updated")
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "ROOM_NOT_FOUND",
                                        "Room with id $roomId was not found"
                                    )
                                )
                            }
                        } else {
                            call.respond(
                                HttpStatusCode.BadRequest, message = ApiError(
                                    "INVALID_ID",
                                    "Invalid user ID"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
