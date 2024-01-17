package com.example.dao.cleanerInfo

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

class CleanerInfoServiceImpl : CleanerInfoService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToCleanerInfo(row: ResultRow): CleanerInfo {
        logger.debug { "$row" }

        return CleanerInfo.findById(row[CleanerInfos.id]) ?: throw ApiError(
            HttpStatusCode.NotFound,
            "Such cleanerInfo doesn't exist"
        )
    }

    override suspend fun addCleanerInfo(cleanerId: Int, hotelId: Int): CleanerInfo = DatabaseFactory.dbQuery {
        logger.debug { "add cleanerInfo for user with id $cleanerId and hotel $hotelId" }

        val user = userService.getUser(cleanerId)
        hotelService.getHotel(hotelId)

        if (user.role.id != ERole.CLEANER.databaseId) {
            error("User with $cleanerId is not cleaner")
        }

        val insertStatement = CleanerInfos.insert {
            it[cleaner_id] = cleanerId
            it[hotel_id] = hotelId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToCleanerInfo(it) } ?: throw ApiError(
                HttpStatusCode.InternalServerError,
                "Internal Server Error"
            )
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getCleanerInfoByCleanerId(id: Int): CleanerInfoDTO = DatabaseFactory.dbQuery {
        logger.debug { "get cleanerInfo by cleaner id: $id" }

        try {
            CleanerInfo.find { CleanerInfos.cleaner_id eq id }
                .map { it.toCleanerInfo() }
                .singleOrNull() ?: throw ApiError(HttpStatusCode.NotFound, "User does not exist")
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getHotelCleanersById(hotelId: Int): List<CleanerInfoDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get all cleaners by hotel $hotelId" }

        try {
            CleanerInfos.select { CleanerInfos.hotel_id eq hotelId }.map { resultRowToCleanerInfo(it).toCleanerInfo() }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }
}

val cleanerInfoService: CleanerInfoService = CleanerInfoServiceImpl().apply {}
