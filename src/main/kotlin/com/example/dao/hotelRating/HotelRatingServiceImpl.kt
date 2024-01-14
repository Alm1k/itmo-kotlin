package com.example.dao.hotelRating

import com.example.dao.DatabaseFactory
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class HotelRatingServiceImpl : HotelRatingService {
    private val logger = KotlinLogging.logger {}

    private fun resultRowToHotelRating(row: ResultRow): HotelRating {
        logger.debug { "$row" }

        return HotelRating.findById(row[HotelRatings.id]) ?: error("Such hotel rating doesn't exist")
    }
    override suspend fun addHotelRating(rate: Int, userId: Int, hotelId: Int): HotelRating? = DatabaseFactory.dbQuery {
        logger.debug { "add hotel rating $rate from user $userId for hotel $hotelId" }

        // todo maybe combine add and update
        val insertStatement = HotelRatings.insert {
            it[HotelRatings.rate] = rate
            it[HotelRatings.user_id] = userId
            it[HotelRatings.hotel_id] = hotelId
        }

        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToHotelRating(it) }
        } catch (e: Throwable) {
            logger.debug { "rating already exists" }
            null
        }
    }

    override suspend fun getUserRatings(userId: Int): List<HotelRatingDTO>? = DatabaseFactory.dbQuery {
        logger.debug { "get user rating for user $userId" }

        try {
            HotelRatings.select { HotelRatings.user_id eq userId }.map { resultRowToHotelRating(it).toHotelRating() }
        } catch (e: Throwable) {
            logger.debug { "No ratings for user" }
            null
        }
    }

    override suspend fun updateHotelRating(rate: Int, userId: Int, hotelId: Int): Int = DatabaseFactory.dbQuery {
        logger.debug { "update hotel rating to $rate from user $userId for hotel $hotelId" }

        try {
            HotelRatings.update {
                it[HotelRatings.rate] = rate
                it[HotelRatings.user_id] = userId
                it[HotelRatings.hotel_id] = hotelId
            }
        } catch (e: Throwable) {
            logger.debug { "rating already exists" }
            error("Hotel does not exists")
        }
    }
}

val hotelRatingService: HotelRatingService = HotelRatingServiceImpl().apply {}