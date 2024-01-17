package com.example.dao.room

import com.example.dao.DatabaseFactory.dbQuery
import com.example.dao.managerInfo.managerInfoService
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RoomServiceImpl : RoomService {

    private val logger = KotlinLogging.logger {}

    override suspend fun mapRoomFromResultRow(row: ResultRow): Room {
        return Room.findById(row[Rooms.id])
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
    }

    override suspend fun getAllRooms(): List<RoomDTO>  = dbQuery{
        logger.debug { "get all rooms" }
        Rooms.selectAll().map { mapRoomFromResultRow(it).toRoom() }
    }

    override suspend fun getRoom(id: Int?): RoomDTO = dbQuery {
        logger.debug { "get user by id: $id" }
        if (id != null) {
            Rooms.select { Rooms.id eq id }
                .map { mapRoomFromResultRow(it).toRoom() }
                .singleOrNull()
                ?: throw ApiError(HttpStatusCode.NotFound, "room with id $id not found")
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "room id is invalid")
        }
    }

    override suspend fun addRoom(number: Int, capacity: Int, floor: Int, price: Double,
                                 isVip: Boolean,  managerId: Int, hotelId: Int): RoomDTO? = dbQuery {
        logger.debug { "add new room" }
        val managerInfo: ManagerInfoDTO
        try {
            managerInfo = managerInfoService.getManagerInfoByManagerId(managerId)
        } catch (e: ApiError) {
            throw ApiError(e.code, e.message)
        }
        val insertStatement = Rooms.insert {
            it[Rooms.number] = number
            it[Rooms.capacity] = capacity
            it[Rooms.floor] = floor
            it[Rooms.price] = price
            it[manager_info_id] = managerInfo.id
            it[hotel_id] = hotelId
        }
        insertStatement.resultedValues?.singleOrNull()?.let { mapRoomFromResultRow(it).toRoom() }
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
    }

    override suspend fun deleteRoom(id: Int?): Boolean = dbQuery {
        logger.debug { "delete room with id: $id" }
        if (id != null) {
           val res = Rooms.deleteWhere { Rooms.id eq id }
            when {
                res > 0 -> return@dbQuery true
                else -> throw ApiError(HttpStatusCode.NotFound, "room with id $id not found")
            }
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "room id is invalid")
        }
    }

    override suspend fun updateRoom(roomId: Int?, managerId: Int, price: Double): Boolean = dbQuery {
        logger.debug { "update room with id $roomId" }
        if (roomId != null) {
            val managerInfo: ManagerInfoDTO
            try {
                managerInfo = managerInfoService.getManagerInfoByManagerId(managerId)
            } catch (e: ApiError) {
                throw ApiError(e.code, e.message)
            }
            val res = Rooms.update({ Rooms.id eq roomId }) {
                it[Rooms.price] = price
                it[manager_info_id] = managerInfo.id
            }
            when {
                res > 0 -> return@dbQuery true
                else -> throw ApiError(HttpStatusCode.NotFound, "room with id $roomId not found")
            }
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "room id is invalid")
        }
    }
}

val roomService: RoomService = RoomServiceImpl().apply {
}
