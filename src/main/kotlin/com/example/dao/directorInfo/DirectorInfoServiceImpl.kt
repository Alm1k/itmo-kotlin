package com.example.dao.directorInfo

import com.example.dao.DatabaseFactory
import com.example.dao.user.userService
import com.example.models.*
import com.example.utils.defaultErrorHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class DirectorInfoServiceImpl : DirectorInfoService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToDirectorInfo(row: ResultRow): DirectorInfo {
        logger.debug { "$row" }

        return DirectorInfo.findById(row[DirectorInfos.id]) ?: throw ApiError(
            HttpStatusCode.NotFound,
            "Such directorInfo doesn't exist"
        )
    }

    override suspend fun getDirectorInfoByDirectorId(id: Int): DirectorInfoDTO = DatabaseFactory.dbQuery {
        logger.debug { "get directorInfo by director id: $id" }

        try {
            DirectorInfos.select { DirectorInfos.director_id eq id }
                .map { resultRowToDirectorInfo(it).toDirectorInfo() }
                .singleOrNull() ?: throw ApiError(HttpStatusCode.NotFound, "Director does not exist")
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun addDirectorInfo(directorId: Int): DirectorInfo = DatabaseFactory.dbQuery {
        logger.debug { "add directorInfo for user with id $directorId" }

        val user = userService.getUser(directorId)

        if (user.role.id != ERole.DIRECTOR.databaseId) {
            throw ApiError(HttpStatusCode.BadRequest, "User is not director")
        }

        val insertStatement = DirectorInfos.insert {
            it[director_id] = directorId
        }

        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToDirectorInfo(it) } ?: throw ApiError(
                HttpStatusCode.InternalServerError,
                "Internal Server Error"
            )
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }
}

val directorInfoService: DirectorInfoService = DirectorInfoServiceImpl().apply {}
