package com.example.dao.room

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.Room
import com.example.models.RoomDTO
import com.example.models.Rooms
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RoomServiceImpl : RoomService {

    private val logger = KotlinLogging.logger {}

    override suspend fun mapRoomFromResultRow(row: ResultRow): Room {
        return Room.findById(row[Rooms.id]) ?: error("Room not found")
    }

    override suspend fun getAllRooms(): List<RoomDTO>  = dbQuery{
        logger.debug { "get all rooms" }
        try {
            Rooms.selectAll().map { mapRoomFromResultRow(it).toRoom() }
        }
        catch(e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}" )
        }
    }

    override suspend fun getRoom(id: Int): RoomDTO? = dbQuery {
        logger.debug { "get user by id: $id" }
        try {
            Rooms.select { Rooms.id eq id }
                .map { mapRoomFromResultRow(it).toRoom() }
                .singleOrNull()
        }
        catch(e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}" )
        }
    }

    override suspend fun addRoom(number: Int, capacity: Int, floor: Int, price: Double,
                                 isVip: Boolean,  managerInfoId: Int, hotelId: Int): RoomDTO? = dbQuery {
        logger.debug { "add new room" }
        val insertStatement = Rooms.insert {
            it[Rooms.number] = number
            it[Rooms.capacity] = capacity
            it[Rooms.floor] = floor
            it[Rooms.price] = price
            it[Rooms.manager_info_id] = managerInfoId
            it[Rooms.hotel_id] = hotelId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let {
                mapRoomFromResultRow(it).toRoom()
            }
        }
        catch (e: Throwable) {
            logger.debug { "${e.message}" }
            null
        }
    }

    override suspend fun deleteRoom(id: Int): Boolean = dbQuery {
        logger.debug { "delete room with id: $id" }
        Rooms.deleteWhere { Rooms.id eq id } > 0
    }

    override suspend fun updateRoom(roomId: Int, managerInfoId: Int, price: Double): Int = dbQuery {
        logger.debug { "update " }
        try {
            Rooms.update({ Rooms.id eq roomId }) {
                it[Rooms.price] = price
                it[Rooms.manager_info_id] = managerInfoId
            }
        } catch (e: Throwable) {
            logger.debug { "user does not exists" }
            error("user does not exists")
        }
    }
}

val roomService: RoomService = RoomServiceImpl().apply {
}
