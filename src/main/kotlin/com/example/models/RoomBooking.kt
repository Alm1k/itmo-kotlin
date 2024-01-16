package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class RoomBooking(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RoomBooking>(RoomBookings)

    var fromDate by RoomBookings.fromDate
    var toDate by RoomBookings.toDate
    var user by User referencedOn RoomBookings.user_id
    var room by Room referencedOn RoomBookings.room_id

    fun toRoomBooking() = RoomBookingsDTO(fromDate, toDate, room.toRoom(), user.id.value)
}

object RoomBookings : IntIdTable("room_bookings") {
    val fromDate = varchar("from_date", 50)
    val toDate = varchar("to_date", 50)
    val room_id = reference("room_id", Rooms.id)
    val user_id = reference("user_id", Users.id)
}

data class RoomBookingsDTO(
    val fromDate: String,
    val toDate: String,
    val room: RoomDTO,
    val userId: Int
)
