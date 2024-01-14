package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class User(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var surname by Users.surname
    var bDay by Users.bDay
    var login by Users.login
    var password by Users.password
    var email by Users.email
    var role by Role referencedOn Users.role_id
    val roomBookings by RoomBooking referrersOn RoomBookings.user_id
    val hotelRatings by HotelRating referrersOn HotelRatings.user_id

    fun toUser() = UserDTO(id.value, name, surname, bDay, login, password, email,
        role.toRole(), roomBookings.map{ it.toRoomBooking() }, hotelRatings.map { it.toHotelRating() })
}

object Users : IntIdTable() {
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val bDay = varchar("bday", 50).nullable()
    val login = varchar("login", 50)
    val password = varchar("password", 200)
    val email = varchar("email", 100).nullable()
    val role_id = reference("role_id", Roles.id)
}

data class UserDTO(
    val id: Int,
    val name: String,
    val surname: String,
    val bDay: String?,
    val login: String,
    val password: String,
    val email: String?,
    val role: RoleDTO,
    val roomBookings: List<RoomBookingsDTO>,
    val ratings: List<HotelRatingDTO>,
)
