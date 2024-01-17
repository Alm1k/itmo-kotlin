package com.example.dao.managerInfo

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class ManagerInfoServiceImpl : ManagerInfoService {

    private val logger = KotlinLogging.logger {}

    override fun mapManagerInfoFromResultRow(row: ResultRow): ManagerInfo {
        logger.debug { "$row" }
        return ManagerInfo.findById(row[ManagerInfos.id])
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
    }
    override suspend fun getManagerInfoByManagerId(id: Int?): ManagerInfoDTO = dbQuery {
        logger.debug { "get managerInfo by manager id: $id" }
        if (id != null) {
            ManagerInfos.select { ManagerInfos.manager_id eq id }
                .map { mapManagerInfoFromResultRow(it).toManagerInfo() }
                .firstOrNull()
                ?: throw ApiError(HttpStatusCode.NotFound, "manager info for manager with id $id not found")
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "user id is invalid")
        }
    }

    override suspend fun addManagerInfo(managerId: Int): ManagerInfo? = dbQuery {
        logger.debug { "add managerInfo for manager with id $managerId" }
        val insertStatement = ManagerInfos.insert {
            it[manager_id] = managerId
        }
        insertStatement.resultedValues?.singleOrNull()?.let { mapManagerInfoFromResultRow(it) }
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
    }
}

val managerInfoService: ManagerInfoService = ManagerInfoServiceImpl().apply {
}
