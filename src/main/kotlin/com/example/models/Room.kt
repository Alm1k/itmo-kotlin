package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Room(val id: Int, val number: Int)

object Rooms : Table() {
    val id = integer("id").autoIncrement()
    val number = integer("number")
    val capacity = integer("capacity")
    val floor = integer("floor")
    val price = double("price")
    val vipStatus = bool("vipStatus").default(false)

    override val primaryKey = PrimaryKey(id)
}
