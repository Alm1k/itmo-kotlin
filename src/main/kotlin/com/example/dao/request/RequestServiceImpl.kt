package com.example.dao.request

import com.example.dao.DatabaseFactory
import com.example.dao.hotel.hotelService
import com.example.dao.room.roomService
import com.example.dao.user.userService
import com.example.models.*
import com.example.utils.defaultErrorHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import org.jetbrains.exposed.sql.*

class RequestServiceImpl : RequestService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToRequest(row: ResultRow): Request {
        logger.debug { "$row" }

        return Request.findById(row[Requests.id]) ?: throw ApiError(
            HttpStatusCode.NotFound,
            "Such request doesn't exist"
        )
    }

    override suspend fun createRequest(
        fromClientId: Int,
        hotelId: Int,
        roomId: Int,
        type: String,
        additionalInfo: String
    ): Request = DatabaseFactory.dbQuery {
        logger.debug { "create request with data fromClientId=$fromClientId, hotelId=$hotelId, roomId=$roomId, type=$type, additionalInfo=$additionalInfo" }

        val requestType = RequestType.find { RequestTypes.type eq type }.singleOrNull() ?: throw ApiError(
            HttpStatusCode.BadRequest,
            "Incorrect type"
        )

        userService.getUser(fromClientId)
        hotelService.getHotel(hotelId)
        roomService.getRoom(roomId)

        val insertStatement = Requests.insert {
            it[from_client_id] = fromClientId
            it[hotel_id] = hotelId
            it[room_id] = roomId
            it[request_type_id] = requestType.id.value
            it[request_status_id] = ERequestStatus.PENDING.databaseId
            it[additional_info] = additionalInfo
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToRequest(it) } ?: throw ApiError(
                HttpStatusCode.InternalServerError, "Internal Server Error"
            )
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getRequestById(requestId: Int): RequestDTO = DatabaseFactory.dbQuery {
        logger.debug { "get request by id = $requestId" }

        try {
            Request.findById(requestId)?.toRequest() ?: throw ApiError(
                HttpStatusCode.NotFound,
                "Request does not exist"
            )
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getAllRequestsByHotelId(hotelId: Int): List<RequestDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get all requests by hotel id = $hotelId" }

        try {
            Requests.select { Requests.hotel_id eq hotelId }.map { resultRowToRequest(it).toRequest() }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun getAllRequestsByClientId(clientId: Int): List<RequestDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get all requests by clientId id = $clientId" }

        try {
            Requests.select { Requests.from_client_id eq clientId }.map { resultRowToRequest(it).toRequest() }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun updateRequestStatus(requestId: Int, status: String): Int = DatabaseFactory.dbQuery {
        logger.debug { "update request status $requestId with status $status" }

        val requestStatus = this.getRequestById(requestId)

        try {
            Requests.update({ Requests.id eq requestId }) {
                it[request_status_id] = requestStatus.id
            }
        } catch (e: Throwable) {
            defaultErrorHandler(e, logger)
        }
    }

    override suspend fun updateRequest(requestId: Int, type: String, additionalInfo: String): Int =
        DatabaseFactory.dbQuery {
            logger.debug { "update request $requestId with type $type and additionalInfo $additionalInfo" }

            val requestType = RequestType.find { RequestTypes.type eq type }.singleOrNull() ?: throw ApiError(
                HttpStatusCode.BadRequest,
                "Incorrect type"
            )
            val request = requestService.getRequestById(requestId)

            if (request.statusId != ERequestStatus.PENDING.databaseId) {
                throw ApiError(HttpStatusCode.Forbidden, "Change request data is possible only in pending status")
            }

            try {
                Requests.update({ Requests.id eq requestId }) {
                    it[request_type_id] = requestType.id.value
                    it[additional_info] = additionalInfo
                }
            } catch (e: Throwable) {
                defaultErrorHandler(e, logger)
            }
        }
}

val requestService: RequestService = RequestServiceImpl().apply {}