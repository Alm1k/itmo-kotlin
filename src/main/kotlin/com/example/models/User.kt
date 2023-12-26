package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

@Serializable
data class User(
    val id: Int, val name: String, val surname: String, val bDay: String?,
    val login: String, val password: String, val email: String?, val roomId: Int?)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val bDay = varchar("bDay", 50).nullable()
    val login = varchar("login", 50)
    val password = varchar("password", 50)
    val email = varchar("email", 100).nullable()
    val roomId = integer("roomId").references(Rooms.id).nullable()

    override val primaryKey = PrimaryKey(id)
}
