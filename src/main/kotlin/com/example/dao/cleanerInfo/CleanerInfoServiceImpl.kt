package com.example.dao.cleanerInfo

import com.example.dao.DatabaseFactory
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class CleanerInfoServiceImpl : CleanerInfoService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToCleanerInfo(row: ResultRow): CleanerInfo {
        logger.debug { "$row" }

        return CleanerInfo.findById(row[CleanerInfos.id]) ?: error("Such cleanerInfo doesn't exist")
    }

    override suspend fun addCleanerInfo(cleanerId: Int, hotelId: Int): CleanerInfo? = DatabaseFactory.dbQuery {
        logger.debug { "add cleanerInfo for user with id $cleanerId" }

        val user = User.findById(cleanerId) ?: error("User with $cleanerId doesn't exist")

        if (user.toUser().role.id != ERole.CLEANER.databaseId) {
            error("User with $cleanerId is not cleaner")
        }

        val insertStatement = CleanerInfos.insert {
            it[cleaner_id] = cleanerId
            it[hotel_id] = hotelId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToCleanerInfo(it) }
        } catch (e: Throwable) {
            logger.debug { "CleanerInfo for this cleaner already exists" }
            null
        }
    }

    override suspend fun getCleanerInfoByCleanerId(id: Int): CleanerInfoDTO? = DatabaseFactory.dbQuery {
        logger.debug { "get cleanerInfo by cleaner id: $id" }

        CleanerInfos.select { CleanerInfos.cleaner_id eq id }
            .map { resultRowToCleanerInfo(it).toCleanerInfo() }
            .singleOrNull()
    }

    override suspend fun getHotelCleanersById(hotelId: Int): List<CleanerInfoDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get all cleaners by hotel $hotelId" }

        try {
            CleanerInfos.selectAll().map{ resultRowToCleanerInfo(it).toCleanerInfo() }
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }
}

val cleanerInfoService: CleanerInfoService = CleanerInfoServiceImpl().apply {}
