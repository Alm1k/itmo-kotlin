package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class HotelRating(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<HotelRating>(HotelRatings)

    var rate by HotelRatings.rate
    val user by User referencedOn HotelRatings.user_id
    val hotel by Hotel referencedOn HotelRatings.hotel_id

    fun toHotelRating() = HotelRatingDTO(id.value, rate, user.id.value, hotel.id.value)
}

object HotelRatings : IntIdTable("hotel_ratings") {
    val rate = integer("rate")
    val hotel_id = reference("hotel_id", Hotels.id)
    val user_id = reference("user_id", Users.id)
}

data class HotelRatingDTO (
    val id: Int,
    val rate: Int,
    val userId: Int,
    val hotelId: Int,
)
