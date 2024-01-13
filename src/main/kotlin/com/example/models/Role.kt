package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class Role(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Role>(Roles)

    var role by Roles.role
    val users by User referrersOn Users.role_id

    fun toRole() = RoleDTO(id.value, role)
}

object Roles : IntIdTable() {
    val role = varchar("role", 50)
}

data class RoleDTO (
    val id: Int,
    val role: String
)
