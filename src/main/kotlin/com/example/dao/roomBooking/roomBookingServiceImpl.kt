package com.example.dao.roomBooking

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
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

    override suspend fun getAllRoomBookingsByUserId(id:Int?): List<RoomBookingsDTO>  = dbQuery {
        logger.debug { "get all roomBookings by user id: $id" }
        if (id != null) {
            RoomBookings.select(RoomBookings.user_id eq id)
                .map { mapRoomBookingFromResultRow(it).toRoomBooking() }
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "roomBooking id is invalid")
        }
    }

    override suspend fun getRoomBooking(id: Int?): RoomBookingsDTO = dbQuery {
        logger.debug { "get roomBooking by id: $id" }
        if (id != null) {
            RoomBookings.select { RoomBookings.id eq id }
                .map { mapRoomBookingFromResultRow(it).toRoomBooking() }
                .singleOrNull()
                ?: throw ApiError(HttpStatusCode.NotFound, "roomBooking with id $id not found")
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "roomBooking id is invalid")
        }
    }

    override suspend fun addRoomBooking(fromDate: String, toDate: String, roomId: Int,
                                        userId: Int): RoomBookingsDTO = dbQuery {
        logger.debug { "add new roomBooking" }
        val insertStatement = RoomBookings.insert {
            it[RoomBookings.fromDate] = fromDate
            it[RoomBookings.toDate] = toDate
            it[user_id] = userId
            it[room_id] = roomId
        }
        val roomBooking = insertStatement.resultedValues?.singleOrNull()
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
        mapRoomBookingFromResultRow(roomBooking).toRoomBooking()
    }

    override suspend fun deleteRoomBooking(id: Int?): Boolean = dbQuery {
        logger.debug { "delete roomBooking with id: $id" }
        if (id != null) {
            val res = RoomBookings.deleteWhere { RoomBookings.id eq id }
            when {
                res > 0 -> return@dbQuery true
                else -> throw ApiError(HttpStatusCode.NotFound, "user with id $id not found")
            }
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "user id is invalid")
         }
    }

    override suspend fun updateRoomBooking(id: Int?, fromDate: String, toDate: String): Boolean = dbQuery {
        logger.debug { "update roomBooking with id $id" }
        if (id != null) {
            val res = RoomBookings.update({ RoomBookings.id eq id }) {
                it[RoomBookings.fromDate] = fromDate
                it[RoomBookings.toDate] = toDate
            }
            when {
                res > 0 -> return@dbQuery true
                else -> throw ApiError(HttpStatusCode.NotFound, "roomBooking with id $id not found")
            }
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "roomBooking id is invalid")
        }
    }
}

val roomBookingService: RoomBookingService = RoomBookingServiceImpl().apply {
}
