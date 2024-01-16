package com.example.dao.roomBooking

import com.example.models.RoomBooking
import com.example.models.RoomBookingsDTO
import org.jetbrains.exposed.sql.ResultRow

interface RoomBookingService {

    suspend fun mapRoomBookingFromResultRow(row: ResultRow): RoomBooking
    suspend fun getRoomBooking(id: Int): RoomBookingsDTO?
    suspend fun getAllRoomBookings(): List<RoomBookingsDTO>
    suspend fun getAllRoomBookingsByUserId(id: Int): List<RoomBookingsDTO>
    suspend fun addRoomBooking(fromDate: String, toDate: String, roomId: Int,
                               userId: Int): RoomBookingsDTO?
    suspend fun deleteRoomBooking(id: Int): Boolean
    suspend fun updateRoomBooking(id: Int, fromDate: String, toDate: String): Int
}
