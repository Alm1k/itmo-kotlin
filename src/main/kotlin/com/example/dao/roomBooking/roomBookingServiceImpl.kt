package com.example.dao.roomBooking

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RoomBookingServiceImpl : RoomBookingService {

    private val logger = KotlinLogging.logger {}

    override suspend fun mapRoomBookingFromResultRow(row: ResultRow): RoomBooking {
        return RoomBooking.findById(row[RoomBookings.id]) ?: error("RoomBooking not found")
    }

    override suspend fun getAllRoomBookings(): List<RoomBookingsDTO>  = dbQuery {
        logger.debug { "get all roomBookings" }
        try {
            RoomBookings.selectAll().map { mapRoomBookingFromResultRow(it).toRoomBooking() }
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }

    override suspend fun getAllRoomBookingsByUserId(id:Int): List<RoomBookingsDTO>  = dbQuery {
        logger.debug { "get all roomBookings by user id: $id" }
        //val query: Query = RoomBookings.select(RoomBookings.user_id eq id)
        try {
            RoomBookings.select(RoomBookings.user_id eq id).map { mapRoomBookingFromResultRow(it).toRoomBooking() }
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }

    override suspend fun getRoomBooking(id: Int): RoomBookingsDTO? = dbQuery {
        logger.debug { "get roomBooking by id: $id" }
        try {
            RoomBookings.select { RoomBookings.id eq id }
                .map { mapRoomBookingFromResultRow(it).toRoomBooking() }
                .singleOrNull()
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }

    override suspend fun addRoomBooking(fromDate: String, toDate: String, roomId: Int,
                                        userId: Int): RoomBookingsDTO? = dbQuery {
        logger.debug { "add new roomBooking" }
        val insertStatement = RoomBookings.insert {
            it[RoomBookings.fromDate] = fromDate
            it[RoomBookings.toDate] = toDate
            it[RoomBookings.user_id] = userId
            it[RoomBookings.room_id] = roomId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let {
                mapRoomBookingFromResultRow(it).toRoomBooking()
            }
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            null
        }
    }

    override suspend fun deleteRoomBooking(id: Int): Boolean = dbQuery {
        logger.debug { "delete roomBooking with id: $id" }
        RoomBookings.deleteWhere { RoomBookings.id eq id } > 0
    }

    override suspend fun updateRoomBooking(id: Int, fromDate: String, toDate: String): Int = dbQuery {
        logger.debug { "update roomBooking with id $id" }
        try {
            RoomBookings.update({ RoomBookings.id eq id }) {
                it[RoomBookings.fromDate] = fromDate
                it[RoomBookings.toDate] = toDate
            }
        } catch (e: Throwable) {
            logger.debug { "roomBooking does not exists" }
            error("roomBooking does not exists")
        }
    }
}

val roomBookingService: RoomBookingService = RoomBookingServiceImpl().apply {
}
