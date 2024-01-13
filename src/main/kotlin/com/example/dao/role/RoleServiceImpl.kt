package com.example.dao.role

import com.example.dao.DatabaseFactory
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.*

class RoleServiceImpl : RoleService {

    private val logger = KotlinLogging.logger {}

    override suspend fun mapRoleFromResultRow(row: ResultRow): Role {
        return Role.findById(row[Roles.id]) ?: error("Role doesn't exist")
    }
    override suspend fun getRole(id: Int): RoleDTO? = DatabaseFactory.dbQuery {
        logger.debug { "get role by id: $id" }
        Roles.select { Roles.id eq id }
            .map { mapRoleFromResultRow(it).toRole() }
            .singleOrNull()
    }
}

val roleService: RoleService = RoleServiceImpl().apply {
}
