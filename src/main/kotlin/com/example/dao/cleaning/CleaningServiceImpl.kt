package com.example.dao.cleaning

import com.example.dao.DatabaseFactory
import com.example.dao.cleanerInfo.cleanerInfoService
import com.example.dao.hotel.hotelService
import com.example.models.ApiError
import com.example.models.Cleaning
import com.example.models.CleaningDTO
import com.example.models.Cleanings
import com.example.utils.defaultErrorHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

class CleaningServiceImpl : CleaningService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToCleaning(row: ResultRow): Cleaning {
        logger.debug { "$row" }

        return Cleaning.findById(row[Cleanings.id]) ?: throw ApiError(
            HttpStatusCode.NotFound,
            "Such cleaning doesn't exist"
        )
    }

    override suspend fun createCleaning(cleanerId: Int, roomId: Int, hotelId: Int): Cleaning =
        DatabaseFactory.dbQuery {
            logger.debug { "Create cleaning for user $cleanerId, hotel $hotelId and room $roomId" }

            val cleanerInfo = cleanerInfoService.getCleanerInfoByCleanerId(cleanerId)
            val hotel = hotelService.getHotel(hotelId)

            if (cleanerInfo.hotelId != hotelId) {
                throw ApiError(HttpStatusCode.BadRequest, "Cleaner $cleanerId is not related to hotel $hotelId")
            }

            if (!hotel.rooms.any { it.id == roomId }) {
                throw ApiError(HttpStatusCode.BadRequest, "Room $roomId is not related to hotel $hotelId")
            }

            val insertStatement = Cleanings.insert {
                it[cleaner_id] = cleanerInfo.id
                it[room_id] = roomId
                it[is_done] = false
                it[creation_date] = LocalDate.now()
                it[hotel_id] = hotelId
            }

            try {
                insertStatement.resultedValues?.singleOrNull()?.let { resultRowToCleaning(it) } ?: throw ApiError(
                    HttpStatusCode.InternalServerError, "Internal Server Error"
                )
            } catch (e: Throwable) {
                defaultErrorHandler(e, logger)
            }
        }

    override suspend fun getCleaningsByCleaner(cleanerId: Int): List<CleaningDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get cleaning by cleanerId: $cleanerId" }

        try {
            Cleanings.select { Cleanings.cleaner_id eq cleanerId }
                .map { resultRowToCleaning(it).toCleaning() }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getCleaningsByHotel(hotelId: Int): List<CleaningDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get cleaning by hotelId: $hotelId" }

        try {
            Cleanings.select { Cleanings.hotel_id eq hotelId }
                .map { resultRowToCleaning(it).toCleaning() }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getCleaningsById(cleaningId: Int): CleaningDTO = DatabaseFactory.dbQuery {
        logger.debug { "get cleaning by id: $cleaningId" }

        try {
            Cleanings.select { Cleanings.id eq cleaningId }
                .map { resultRowToCleaning(it).toCleaning() }
                .singleOrNull() ?: throw ApiError(HttpStatusCode.NotFound, "Cleaning does not found")
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun completeCleaning(cleaningId: Int): Int = DatabaseFactory.dbQuery {
        logger.debug { "complete cleaning for cleaning $cleaningId" }

        this.getCleaningsById(cleaningId)

        try {
            Cleanings.update({ Cleanings.id eq cleaningId }) {
                it[is_done] = true
            }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }
}

val cleaningService: CleaningService = CleaningServiceImpl().apply {}