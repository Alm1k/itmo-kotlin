package com.example.dao.hotel

import com.example.dao.DatabaseFactory
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.*

class HotelServiceImpl : HotelService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToHotel(row: ResultRow): Hotel {
        logger.debug { "$row" }

        return Hotel.findById(row[Hotels.id]) ?: error("Such hotel doesn't exist")
    }
    override suspend fun addHotel(
        name: String,
        stageCount: Int,
        directorId: Int
    ): Hotel? = DatabaseFactory.dbQuery {
        logger.debug { "add hotel with data name=$name, stageCount=$stageCount, directorId=$directorId" }

        val insertStatement = Hotels.insert {
            it[Hotels.name] = name
            it[Hotels.stageCount] = stageCount
            it[Hotels.director_info_id] = directorId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToHotel(it) }
        } catch (e: Throwable) {
            logger.debug { "hotel already exists" }
            null
        }
    }

    override suspend fun changeDirector(hotelId: Int, directorId: Int): Int? = DatabaseFactory.dbQuery {
        logger.debug { "change director for hotel $hotelId on director $directorId" }

        DirectorInfo.findById(directorId) ?: error("Director does not exists")
        try {
            Hotels.update({ Hotels.id eq hotelId }) {
                it[director_info_id] = directorId
            }
        } catch (e: Throwable) {
            logger.debug { "hotel does not exists" }
            error("Hotel does not exists")
        }
    }

    override suspend fun getHotel(hotelId: Int): HotelDTO? = DatabaseFactory.dbQuery {
        logger.debug { "get hotel with id $hotelId" }
        try {
            Hotels.select { Hotels.id eq hotelId }
                .map { resultRowToHotel(it).toHotel() }
                .singleOrNull()
        } catch (e: Throwable) {
        logger.debug { "hotel does not exists ${e.message} ${e.cause}" }
        null
    }
    }

    override suspend fun getAllHotels(): List<HotelDTO>  = DatabaseFactory.dbQuery {
        logger.debug { "get all hotels" }

        try {
            Hotels.selectAll().map{ resultRowToHotel(it).toHotel() }
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }

    override suspend fun updateHotel(hotelId: Int, name: String, stageCount: Int): Int? = DatabaseFactory.dbQuery {
        logger.debug { "update hotel $hotelId info name = $name, stageCount = $stageCount" }

        try {
            Hotels.update({ Hotels.id eq hotelId }) {
                it[Hotels.name] = name
                it[Hotels.stageCount] = stageCount
            }
        } catch (e: Throwable) {
            logger.debug { "hotel does not exists" }
            error("Hotel does not exists")
        }
    }
}


val hotelService: HotelService = HotelServiceImpl().apply {}