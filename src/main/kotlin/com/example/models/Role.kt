package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Role(val id: Int, val role: ERole)

object Roles : Table() {
    val id = integer("id").autoIncrement()
    val role = varchar("role", 50)

    override val primaryKey = PrimaryKey(id)
}
