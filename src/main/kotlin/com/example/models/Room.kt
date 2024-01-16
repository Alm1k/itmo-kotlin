package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class Room(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Room>(Rooms)

    var number by Rooms.number
    var capacity by Rooms.capacity
    var floor by Rooms.floor
    var price by Rooms.price
    var isVip by Rooms.isVip
    val roomBookings by RoomBooking referrersOn RoomBookings.room_id
    var managerInfo by ManagerInfo referencedOn Rooms.manager_info_id
    var hotel by Hotel referencedOn Rooms.hotel_id

    fun toRoom() = RoomDTO(id.value, number, capacity, floor, price, isVip,
        managerInfo.id.value, hotel.id.value)
}

object Rooms : IntIdTable() {
    val number = integer("number")
    val capacity = integer("capacity")
    val floor = integer("floor")
    val price = double("price")
    val isVip = bool("is_vip").default(false)
    val manager_info_id = reference("manager_info_id", ManagerInfos.id)
    val hotel_id = reference("hotel_id", Hotels.id)
}

data class RoomDTO(
    val id: Int,
    val number: Int,
    val capacity: Int,
    val floor: Int,
    val price: Double,
    val isVip: Boolean,
    val managerInfoId: Int,
    val hotelId: Int
)
