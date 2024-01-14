package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

class Cleaning(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Cleaning>(Cleanings)

    var creationDate by Cleanings.creation_date
    var cleanerId by CleanerInfo referencedOn Cleanings.cleaner_id
    var isDone by Cleanings.is_done
    var roomId by Room referencedOn Cleanings.room_id
    var hotelId by Hotel referencedOn Cleanings.hotel_id

    fun toCleaning() = CleaningDTO(id.value, creationDate.toString(), cleanerId.id.value, isDone, roomId.id.value, hotelId.id.value)

    // todo maybe add complete_date
}

object Cleanings : IntIdTable("cleanings") {
    val creation_date = date("creation_date")
    val cleaner_id = reference("cleaner_id", CleanerInfos.id)
    val is_done = bool("is_done")
    val room_id = reference("room_id", Rooms.id)
    val hotel_id = reference("hotel_id", Hotels.id)
}

data class CleaningDTO(
    val id: Int,
    val creationDate: String,
    val cleanerId: Int,
    val isDone: Boolean,
    val roomId: Int,
    val hotelId: Int,
)