package com.example.routes

import com.example.dao.roomBooking.roomBookingService
import com.example.models.ApiError
import com.example.models.ERole
import com.example.models.RoomBookingsDTO
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class RoomBookingUpdateRequest(val id: Int, val fromDate: String, val toDate: String)
data class RoomBookingRequest(val fromDate: String, val toDate: String, val roomId: Int, val userId: Int)
fun Route.roomBookingsRouting() {

    route("/api/room-bookings") {

        authenticate {

            authorized(
                rolesMap.getValue(ERole.DIRECTOR).toString(),
                rolesMap.getValue(ERole.MANAGER).toString()
            ) {

                get {
                    call.respond(roomBookingService.getAllRoomBookings())
                }
            }
        }

        authenticate() {

            authorized(
                rolesMap.getValue(ERole.USER).toString(),
                rolesMap.getValue(ERole.DIRECTOR).toString(),
                rolesMap.getValue(ERole.MANAGER).toString()

            ) {

                post {
                    val roomBooking = call.receive<RoomBookingRequest>()
                    roomBookingService.addRoomBooking(
                        roomBooking.fromDate,
                        roomBooking.toDate,
                        roomBooking.roomId,
                        roomBooking.userId
                    ) ?: call.respond("new roomBooking created")
                }

                route("/{roomBookingId}") {

                    get {
                        val id =
                            call.parameters["roomBookingId"]?.toIntOrNull()
                                ?: throw IllegalArgumentException("Invalid ID")
                        try {
                            val roomBooking: RoomBookingsDTO? = roomBookingService.getRoomBooking(id)
                            if (roomBooking != null) {
                                call.respond(HttpStatusCode.OK, roomBooking)
                            }
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.NotFound, message = ApiError(
                                    "ROOM_BOOKING_NOT_FOUND",
                                    "RoomBooking with id $id was not found"
                                )
                            )
                        }
                    }

                    delete {
                        val roomBookingId = call.parameters["roomBookingId"]?.toIntOrNull()

                        if (roomBookingId != null) {
                            val deleted = roomBookingService.deleteRoomBooking(roomBookingId)
                            if (deleted) {
                                call.respond(HttpStatusCode.OK, "RoomBooking deleted")
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "ROOM_BOOKING_NOT_FOUND",
                                        "RoomBooking with id $roomBookingId was not found"
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
                        val roomBookingId = call.parameters["roomBookingId"]?.toIntOrNull()

                        if (roomBookingId != null) {
                            val updateInfo = call.receive<RoomBookingUpdateRequest>()
                            val updated = roomBookingService.updateRoomBooking(
                                roomBookingId,
                                updateInfo.fromDate,
                                updateInfo.toDate
                            )
                            if (updated > 0) {
                                call.respond(HttpStatusCode.OK, "RoomBooking info updated")
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound, message = ApiError(
                                        "ROOM_BOOKING_NOT_FOUND",
                                        "RoomBooking with id $roomBookingId was not found"
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

                route("/client/{clientId}") {

                    get {
                        val id =
                            call.parameters["clientId"]?.toIntOrNull()
                                ?: throw IllegalArgumentException("Invalid ID")
                        try {
                            val roomBookings: List<RoomBookingsDTO> =
                                roomBookingService.getAllRoomBookingsByUserId(id)
                            call.respond(HttpStatusCode.OK, roomBookings)
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.NotFound, message = ApiError(
                                    "CLIENT_ROOM_BOOKINGS_NOT_FOUND",
                                    "RoomBookings for client with id $id were not found"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
