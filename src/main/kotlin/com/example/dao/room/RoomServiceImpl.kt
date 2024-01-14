package com.example.dao.room

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.Room
import com.example.models.RoomDTO
import com.example.models.Rooms
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RoomServiceImpl : RoomService {

    private val logger = KotlinLogging.logger {}

    override suspend fun mapRoomFromResultRow(row: ResultRow): Room {
        return Room.findById(row[Rooms.id]) ?: error("Room not found")
    }

    override suspend fun getAllRooms(): List<RoomDTO>  = withContext(Dispatchers.IO){
        logger.debug { "get all rooms" }
        return@withContext dbQuery {  Rooms.selectAll().map{ mapRoomFromResultRow(it).toRoom() } }
    }

    override suspend fun getRoom(id: Int): RoomDTO? = dbQuery {
        logger.debug { "get user by id: $id" }
        Rooms.select { Rooms.id eq id }
            .map { mapRoomFromResultRow(it).toRoom() }
            .singleOrNull()
    }

    override suspend fun addRoom(number: Int, capacity: Int, floor: Int, price: Double,
                                 isVip: Boolean,  managerInfoId: Int): RoomDTO? = dbQuery {
        logger.debug { "add new room" }
        val insertStatement = Rooms.insert {
            it[Rooms.number] = number
            it[Rooms.capacity] = capacity
            it[Rooms.floor] = floor
            it[Rooms.price] = price
            it[Rooms.manager_info_id] = managerInfoId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let {
                mapRoomFromResultRow(it).toRoom()
            }
        }
        catch (e: Throwable) {
            logger.debug { "$e" }
            null
        }
    }

    override suspend fun deleteRoom(id: Int): Boolean = dbQuery {
        logger.debug { "delete room with id: $id" }
        Rooms.deleteWhere { Rooms.id eq id } > 0
    }

}

val roomService: RoomService = RoomServiceImpl().apply {
}
