package com.example.dao.role

import com.example.models.Role
import com.example.models.RoleDTO
import org.jetbrains.exposed.sql.ResultRow

interface RoleService {
    suspend fun mapRoleFromResultRow(row: ResultRow): Role
    suspend fun getRole(id: Int): RoleDTO?
}
