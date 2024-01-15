package com.example.dao.directorInfo

import com.example.dao.DatabaseFactory
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class DirectorInfoServiceImpl : DirectorInfoService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToDirectorInfo(row: ResultRow): DirectorInfo {
        logger.debug { "$row" }

        return DirectorInfo.findById(row[DirectorInfos.id]) ?: error("Such directorInfo doesn't exist")
    }
    override suspend fun getDirectorInfoByDirectorId(id: Int): DirectorInfoDTO? = DatabaseFactory.dbQuery {
        logger.debug { "get directorInfo by director id: $id" }

        DirectorInfos.select { DirectorInfos.director_id eq id }
            .map { resultRowToDirectorInfo(it).toDirectorInfo() }
            .singleOrNull()
    }

    override suspend fun addDirectorInfo(directorId: Int): DirectorInfo? = DatabaseFactory.dbQuery {
        logger.debug { "add directorInfo for user with id $directorId" }

        val user = User.findById(directorId) ?: error("User with $directorId doesn't exist")

        if (user.toUser().role.id != ERole.DIRECTOR.databaseId) {
            error("User with $directorId is not director")
        }

        val insertStatement = DirectorInfos.insert {
            it[director_id] = directorId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToDirectorInfo(it) }
        } catch (e: Throwable) {
            logger.debug { "DirectorInfo for this director already exists" }
            null
        }
    }
}

val directorInfoService: DirectorInfoService = DirectorInfoServiceImpl().apply {}
