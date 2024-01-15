package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedIterable
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun calculateRating(hotelRatings: SizedIterable<HotelRating>): Double? {
    if (hotelRatings.empty()) {
        return null
    }

    val dfs = DecimalFormatSymbols(Locale.getDefault())
    dfs.setDecimalSeparator('.')
    val df = DecimalFormat("#.00", dfs)
    df.roundingMode = RoundingMode.FLOOR
    val sum = hotelRatings.sumOf { it.toHotelRating().rate }

    return df.format(sum.toDouble() / hotelRatings.count()).toDouble()
}

class Hotel(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Hotel>(Hotels)

    var name by Hotels.name
    var stageCount by Hotels.stageCount
    var directorInfo by DirectorInfo referencedOn Hotels.director_info_id
    val rooms by Room referrersOn Rooms.hotel_id
    val ratings by HotelRating referrersOn HotelRatings.hotel_id

    fun toHotel() = HotelDTO(id.value, name, rooms.map { it.toRoom() }, stageCount, directorInfo.id.value, calculateRating(ratings))
}

object Hotels : IntIdTable("hotels") {
    val name = varchar("name", 50)
    val stageCount = integer("stage_count")
    val director_info_id = reference("director_info_id", DirectorInfos.id)
}

data class HotelDTO(
    val id: Int,
    val name: String,
    val rooms: List<RoomDTO>,
    val stageCount: Int,
    val directorInfoId: Int,
    val rating: Double?,
)
