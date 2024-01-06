package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(
    val id: Int, val name: String, val surname: String, val bDay: String?,
    val login: String, val roleId: Int, val password: String, val email: String?,
    //val roomId: Int?
     )

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val bDay = varchar("bday", 50).nullable()
    val login = varchar("login", 50)
    val password = varchar("password", 50)
    val email = varchar("email", 100).nullable()
    val roleId = integer("role_id").references(Roles.id)
    //val roomId = integer("room_id").references(Rooms.id).nullable() // ->bookings

    override val primaryKey = PrimaryKey(id)
}
