package com.example.routes

import com.example.dao.roomBooking.roomBookingService
import com.example.models.ApiError
import com.example.models.ERole
import com.example.models.rolesMap
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.reflect.full.createInstance

data class RoomBookingUpdateRequest(val id: Int, val fromDate: String, val toDate: String) {
    constructor(): this(0, "", "")
}
data class RoomBookingRequest(val fromDate: String, val toDate: String,
                              val roomId: Int, val userId: Int) {
    constructor(): this( "", "", 0, 0)
}
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
                    var roomBooking = RoomBookingRequest::class.createInstance()
                    try {
                        roomBooking = call.receive<RoomBookingRequest>()
                    } catch (e: Throwable) {
                        call.respond(HttpStatusCode.UnprocessableEntity,
                            "failed to convert request body to class RoomBookingRequest")
                    }
                    try {
                        roomBookingService.addRoomBooking(
                            roomBooking.fromDate,
                            roomBooking.toDate,
                            roomBooking.roomId,
                            roomBooking.userId
                        )
                        call.respond("new roomBooking created")
                    } catch(e: ApiError) {
                        call.respond(e.code, e.message)
                    }
                }

                route("/{roomBookingId}") {

                    get {
                        val id = call.parameters["roomBookingId"]?.toIntOrNull()
                        try {
                            val roomBooking = roomBookingService.getRoomBooking(id)
                            call.respond(HttpStatusCode.OK, roomBooking)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }

                    delete {
                        val roomBookingId = call.parameters["roomBookingId"]?.toIntOrNull()

                        try {
                            roomBookingService.deleteRoomBooking(roomBookingId)
                            call.respond(HttpStatusCode.OK, "RoomBooking deleted")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }

                    patch {
                        val roomBookingId = call.parameters["roomBookingId"]?.toIntOrNull()

                        var updateInfo = RoomBookingUpdateRequest::class.createInstance()
                        try {
                            updateInfo = call.receive<RoomBookingUpdateRequest>()
                        } catch (e: Throwable) {
                            call.respond(HttpStatusCode.UnprocessableEntity,
                                "failed to convert request body to class RoomBookingUpdateRequest")
                        }
                        try {
                            roomBookingService.updateRoomBooking(
                                roomBookingId,
                                updateInfo.fromDate,
                                updateInfo.toDate
                            )
                            call.respond(HttpStatusCode.OK, "roomBooking updated")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }

                route("/client/{clientId}") {

                    get {
                        val id = call.parameters["clientId"]?.toIntOrNull()
                        try {
                            val roomBookings = roomBookingService.getAllRoomBookingsByUserId(id)
                            call.respond(HttpStatusCode.OK, roomBookings)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }
                }
            }
        }
    }
}
