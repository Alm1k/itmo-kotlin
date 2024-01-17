package com.example.dao.hotelRating

import com.example.dao.DatabaseFactory
import com.example.dao.hotel.hotelService
import com.example.dao.user.userService
import com.example.models.*
import com.example.utils.defaultErrorHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class HotelRatingServiceImpl : HotelRatingService {
    private val logger = KotlinLogging.logger {}

    private fun resultRowToHotelRating(row: ResultRow): HotelRating {
        logger.debug { "$row" }

        return HotelRating.findById(row[HotelRatings.id]) ?: throw ApiError(
            HttpStatusCode.NotFound,
            "Such hotel rating doesn't exist"
        )
    }

    override suspend fun addHotelRating(rate: Int, userId: Int, hotelId: Int): HotelRating = DatabaseFactory.dbQuery {
        logger.debug { "add hotel rating $rate from user $userId for hotel $hotelId" }

        userService.getUser(userId)
        hotelService.getHotel(hotelId)

        val insertStatement = HotelRatings.insert {
            it[HotelRatings.rate] = rate
            it[HotelRatings.user_id] = userId
            it[HotelRatings.hotel_id] = hotelId
        }

        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToHotelRating(it) } ?: throw ApiError(
                HttpStatusCode.InternalServerError, "Internal Server Error"
            )
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getUserRatings(userId: Int): List<HotelRatingDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get user rating for user $userId" }

        try {
            HotelRatings.select { HotelRatings.user_id eq userId }.map { resultRowToHotelRating(it).toHotelRating() }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun updateHotelRating(ratingId: Int, rate: Int, userId: Int, hotelId: Int): Int =
        DatabaseFactory.dbQuery {
            logger.debug { "update hotel rating to $rate from user $userId for hotel $hotelId" }

            userService.getUser(userId)
            hotelService.getHotel(hotelId)

            try {
                HotelRatings.update({ HotelRatings.id eq ratingId }) {
                    it[HotelRatings.rate] = rate
                    it[HotelRatings.user_id] = userId
                    it[HotelRatings.hotel_id] = hotelId
                }
            } catch (e: Throwable) {
                defaultErrorHandler(e, logger)
            }
        }
}

val hotelRatingService: HotelRatingService = HotelRatingServiceImpl().apply {}