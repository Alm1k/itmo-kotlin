package com.example.dao.role

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import org.jetbrains.exposed.sql.*

class RoleServiceImpl : RoleService {

    private val logger = KotlinLogging.logger {}
    override suspend fun mapRoleFromResultRow(row: ResultRow): Role {
        logger.debug { "$row" }
        return Role.findById(row[Roles.id])
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
    }
    override suspend fun getRole(id: Int?): RoleDTO = dbQuery {
        logger.debug { "get role by id: $id" }
        if (id != null) {
        Roles.select { Roles.id eq id }
            .map { mapRoleFromResultRow(it).toRole() }
            .singleOrNull()
            ?: throw ApiError(HttpStatusCode.NotFound, "role with id $id not found")
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "role id is invalid")
        }
    }
}

val roleService: RoleService = RoleServiceImpl().apply {
}
