package com.example.routes

import com.example.dao.cleanerInfo.cleanerInfoService
import com.example.dao.cleaning.cleaningService
import com.example.dao.hotel.hotelService
import com.example.dao.hotelRating.hotelRatingService
import com.example.dao.request.requestService
import com.example.models.*
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class SetHotelRequest(val name: String, val stageCount: Int, val userId: Int)
data class UpdateDirectorRequest(val userId: Int)

data class RatingRequest(val userId: Int, val rate: Int)

fun Route.hotelRouting() {

    route("/api/hotels") {

        authenticate {

            authorized(
                rolesMap.getValue(ERole.DIRECTOR).toString(),
                rolesMap.getValue(ERole.MANAGER).toString()
            ) {

                get {
                    call.respond(HttpStatusCode.OK, hotelService.getAllHotels())
                }

                post {
                    val data: SetHotelRequest

                    try {
                        data = call.receive<SetHotelRequest>()
                    } catch (e: Throwable) {
                        call.respond(
                            HttpStatusCode.UnprocessableEntity,
                            "failed to convert request body to class SetHotelRequest"
                        )

                        return@post
                    }

                    try {
                        hotelService.addHotel(
                            data.name,
                            data.stageCount,
                            data.userId,
                        )
                        call.respond("Hotel for director with id ${data.userId} created")
                    } catch (e: ApiError) {
                        call.respond(e.code, e.message)
                    }
                }

                route("/{hotelId}") {

                    post {
                        val hotelId =
                            call.parameters["hotelId"]?.toIntOrNull() ?: throw java.lang.IllegalArgumentException(
                                "Invalid Hotel Id"
                            )
                        val userId: Int

                        try {
                            userId = call.receive<UpdateDirectorRequest>().userId
                        } catch (e: Throwable) {
                            call.respond(
                                HttpStatusCode.UnprocessableEntity,
                                "failed to convert request body to class UpdateDirectorRequest"
                            )

                            return@post
                        }

                        try {
                            hotelService.changeDirector(hotelId, userId)

                            call.respond("Director for hotel $hotelId changed on $userId")
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }

                    route("/requests") {
                        get {
                            val id =
                                call.parameters["hotelId"]?.toIntOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")

                            try {
                                call.respond(HttpStatusCode.OK, requestService.getAllRequestsByHotelId(id))
                            } catch (e: ApiError) {
                                call.respond(e.code, e.message)
                            }
                        }
                    }

                    route("/cleaners") {
                        get {
                            val hotelId = call.parameters["hotelId"]?.toIntOrNull()
                                ?: throw java.lang.IllegalArgumentException("Invalid Hotel Id")

                            try {
                                call.respond(HttpStatusCode.OK, cleanerInfoService.getHotelCleanersById(hotelId))
                            } catch (e: ApiError) {
                                call.respond(e.code, e.message)
                            }
                        }
                    }

                    route("/cleanings") {
                        get {
                            val hotelId = call.parameters["hotelId"]?.toIntOrNull()
                                ?: throw java.lang.IllegalArgumentException("Invalid Hotel Id")

                            try {
                                call.respond(HttpStatusCode.OK, cleaningService.getCleaningsByHotel(hotelId))
                            } catch (e: ApiError) {
                                call.respond(e.code, e.message)
                            }
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
                route("/{hotelId}") {
                    get {
                        val id =
                            call.parameters["hotelId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")

                        try {
                            val hotel: HotelDTO = hotelService.getHotel(id)

                            call.respond(HttpStatusCode.OK, hotel)
                        } catch (e: ApiError) {
                            call.respond(e.code, e.message)
                        }
                    }

                    route("/rate") {
                        post {
                            val hotelId = call.parameters["hotelId"]?.toIntOrNull()
                                ?: throw java.lang.IllegalArgumentException("Invalid Hotel Id")
                            val data: RatingRequest

                            try {
                                data = call.receive<RatingRequest>()
                            } catch (e: Throwable) {
                                call.respond(
                                    HttpStatusCode.UnprocessableEntity,
                                    "failed to convert request body to class RatingRequest"
                                )

                                return@post
                            }

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
                            } catch (e: ApiError) {
                                call.respond(e.code, e.message)
                            }
                        }
                    }
                }
            }
        }
    }
}
