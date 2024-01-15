package com.example.dao.room

import com.example.models.*
import org.jetbrains.exposed.sql.ResultRow

interface RoomService {
    suspend fun mapRoomFromResultRow(row: ResultRow): Room
    suspend fun getRoom(id: Int): RoomDTO?
    suspend fun getAllRooms(): List<RoomDTO>
    suspend fun addRoom(number: Int, capacity: Int, floor: Int, price: Double,
                        isVip: Boolean,  managerInfoId: Int, hotelId: Int): RoomDTO?
    suspend fun deleteRoom(id: Int): Boolean
    suspend fun updateRoom(roomId: Int, managerInfoId: Int, price: Double): Int
}
