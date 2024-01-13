package com.example.dao.managerInfo

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class ManagerInfoServiceImpl : ManagerInfoService {

    private val logger = KotlinLogging.logger {}

    override fun mapManagerInfoFromResultRow(row: ResultRow): ManagerInfo {
        logger.debug { "$row" }
        return ManagerInfo.findById(row[ManagerInfos.id]) ?: error("Such managerInfo doesn't exist")
    }
    override suspend fun getManagerInfoByManagerId(id: Int): ManagerInfoDTO? = dbQuery {
        logger.debug { "get managerInfo by manager id: $id" }
        ManagerInfos.select { ManagerInfos.manager_id eq id }
            .map { mapManagerInfoFromResultRow(it).toManagerInfo() }
            .singleOrNull()
    }

    override suspend fun addManagerInfo(managerId: Int): ManagerInfo? = dbQuery {
        logger.debug { "add managerInfo for manager with id $managerId" }
        val insertStatement = ManagerInfos.insert {
            it[ManagerInfos.manager_id] = managerId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let { mapManagerInfoFromResultRow(it) }
        } catch (e: Throwable) {
            logger.debug { "managerInfo for this manager already exists" }
            null
        }
    }
}

val managerInfoService: ManagerInfoService = ManagerInfoServiceImpl().apply {
}
