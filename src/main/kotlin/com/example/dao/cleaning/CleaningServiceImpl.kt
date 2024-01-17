package com.example.dao.cleaning

import com.example.dao.DatabaseFactory
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

class CleaningServiceImpl : CleaningService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToCleaning(row: ResultRow): Cleaning {
        logger.debug { "$row" }

        return Cleaning.findById(row[Cleanings.id]) ?: error("Such cleaning doesn't exist")
    }

    override suspend fun createCleaning(cleanerId: Int, roomId: Int, hotelId: Int): Cleaning? =
        DatabaseFactory.dbQuery {
            logger.debug { "Create cleaning for user $cleanerId, hotel $hotelId and room $roomId" }

            val cleanerInfo = CleanerInfo.find { CleanerInfos.cleaner_id eq cleanerId }.singleOrNull()
                ?: error("User $cleanerId doesn't have cleanerInfo or doesn't exists")

            if (cleanerInfo.toCleanerInfo().hotelId != hotelId) {
                error("Cleaner $cleanerId is not related to hotel $hotelId")
            }

            val insertStatement = Cleanings.insert {
                it[cleaner_id] = cleanerInfo.id
                it[room_id] = roomId
                it[is_done] = false
                it[creation_date] = LocalDate.now()
                it[hotel_id] = hotelId
            }

            try {
                insertStatement.resultedValues?.singleOrNull()?.let { resultRowToCleaning(it) }
            } catch (e: Throwable) {
                logger.debug { "Cleaning for this room already exists" }
                null
            }
        }

    override suspend fun getCleaningsByCleaner(cleanerId: Int): List<CleaningDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get cleaning by cleanerId: $cleanerId" }

        Cleanings.select { Cleanings.cleaner_id eq cleanerId }
            .map { resultRowToCleaning(it).toCleaning() }
    }

    override suspend fun getCleaningsByHotel(hotelId: Int): List<CleaningDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get cleaning by hotelId: $hotelId" }

        Cleanings.select { Cleanings.hotel_id eq hotelId }
            .map { resultRowToCleaning(it).toCleaning() }
    }

    override suspend fun getCleaningsById(cleaningId: Int): CleaningDTO? = DatabaseFactory.dbQuery {
        logger.debug { "get cleaning by id: $cleaningId" }

        Cleanings.select { Cleanings.id eq cleaningId }
            .map { resultRowToCleaning(it).toCleaning() }
            .singleOrNull()
    }

    override suspend fun completeCleaning(cleaningId: Int): Int = DatabaseFactory.dbQuery {
        logger.debug { "complete cleaning for cleaning $cleaningId" }

        try {
            Cleanings.update({ Cleanings.id eq cleaningId }) {
                it[is_done] = true
            }
        } catch (e: Throwable) {
            logger.debug { "cleaning does not exists" }
            error("Cleaning does not exists")
        }
    }
}

val cleaningService: CleaningService = CleaningServiceImpl().apply {}