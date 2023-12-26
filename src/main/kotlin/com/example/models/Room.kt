package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Room(val id: Int, val number: Int)

object Rooms : Table() {
    val id = integer("id").autoIncrement()
    val number = integer("number")
    val peopleCount = integer("peopleCount")
    val stage = integer("stage")
    val price = double("price")
    val vipStatus = bool("vipStatus").default(false)

    override val primaryKey = PrimaryKey(id)
}
