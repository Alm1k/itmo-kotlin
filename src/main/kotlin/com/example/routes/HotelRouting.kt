package com.example.routes

import com.example.dao.cleanerInfo.cleanerInfoService
import com.example.dao.cleaning.cleaningService
import com.example.dao.hotel.hotelService
import com.example.dao.hotelRating.hotelRatingService
import com.example.models.*
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class SetHotelRequest(val name: String, val stageCount: Int, val directorId: Int)
data class UpdateDirectorRequest(val directorId: Int)

data class RatingRequest(val userId: Int, val rate: Int)
fun Route.hotelRouting() {

    authenticate {

        route("/api/hotel") {
            authorized(rolesMap.getValue(ERole.DIRECTOR).toString(), rolesMap.getValue(ERole.MANAGER).toString()) {

                post {
                    val creds = call.receive<SetHotelRequest>()

                    try {
                        hotelService.addHotel(
                            creds.name,
                            creds.stageCount,
                            creds.directorId,
                        ) ?: call.respond("Hotel for director with id ${creds.directorId} created")
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest, message = ApiError(
                                "HOTEL_ALREADY_EXISTS",
                                "$e: Hotel already exists"
                            )
                        )
                    }
                }

                route("/{hotelId") {

                    post {
                        val hotelId =
                            call.parameters["hotelId"]?.toIntOrNull() ?: throw java.lang.IllegalArgumentException(
                                "Invalid Hotel Id"
                            )
                        val directorId = call.receive<UpdateDirectorRequest>().directorId

                        try {
                            hotelService.changeDirector(hotelId, directorId)
                                ?: call.respond("Director for hotel $hotelId changed on $directorId")
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.NotFound, message = ApiError(
                                    "NOT_FOUND",
                                    "${e.message}"
                                )
                            )
                        }
                    }

                    route("/cleaners") {
                        get {
                            val hotelId = call.parameters["hotelId"]?.toIntOrNull()
                                ?: throw java.lang.IllegalArgumentException("Invalid Hotel Id")

                            call.respond(HttpStatusCode.OK, cleanerInfoService.getHotelCleanersById(hotelId))
                        }
                    }

                    route("/cleanings") {
                        get {
                            val hotelId = call.parameters["hotelId"]?.toIntOrNull()
                                ?: throw java.lang.IllegalArgumentException("Invalid Hotel Id")

                            call.respond(HttpStatusCode.OK, cleaningService.getCleaningsByHotel(hotelId))
                        }
                    }
                }
            }

            get {
                call.respond(HttpStatusCode.OK, hotelService.getAllHotels())
            }

            route("/{hotelId}") {
                get {
                    val id = call.parameters["hotelId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")

                    try {
                        val hotel: HotelDTO? = hotelService.getHotel(id)

                        if (hotel != null) {
                            call.respond(HttpStatusCode.OK, hotel)
                        }
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.NotFound, message = ApiError(
                                "HOTEL_NOT_FOUND",
                                "$e: Hotel with id $id was not found"
                            )
                        )
                    }
                }

                route("/rate") {
                    post {
                        val hotelId = call.parameters["hotelId"]?.toIntOrNull()
                            ?: throw java.lang.IllegalArgumentException("Invalid Hotel Id")
                        val data = call.receive<RatingRequest>()

                        try {
                            hotelRatingService.addHotelRating(
                                data.rate,
                                data.userId,
                                hotelId,
                            )
                            call.respond(
                                HttpStatusCode.OK,
                                "Rating for hotel ${data.userId} from user ${data.userId} created"
                            )
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.BadRequest, message = ApiError(
                                    "",
                                    "$e: Rating already exists"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}