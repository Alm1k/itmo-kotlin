package com.example.dao.request

import com.example.dao.DatabaseFactory
import com.example.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class RequestServiceImpl : RequestService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToRequest(row: ResultRow): Request {
        logger.debug { "$row" }

        return Request.findById(row[Requests.id]) ?: error("Such request doesn't exist")
    }

    override suspend fun createRequest(
        fromClientId: Int,
        hotelId: Int,
        roomId: Int,
        type: String,
        additionalInfo: String
    ): Request? = DatabaseFactory.dbQuery {
        logger.debug { "create request with data fromClientId=$fromClientId, hotelId=$hotelId, roomId=$roomId, type=$type, additionalInfo=$additionalInfo" }

        val requestType = RequestType.find { RequestTypes.type eq type }.singleOrNull() ?: error("Incorrect type")
        val insertStatement = Requests.insert {
            it[from_client_id] = fromClientId
            it[hotel_id] = hotelId
            it[room_id] = roomId
            it[request_type_id] = requestType.id.value
            it[request_status_id] = ERequestStatus.PENDING.databaseId
            it[additional_info] = additionalInfo
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let { resultRowToRequest(it) }
        } catch (e: Throwable) {
            logger.debug { e.localizedMessage }
            null
        }
    }

    override suspend fun getRequestById(requestId: Int): RequestDTO = DatabaseFactory.dbQuery {
        logger.debug { "get request by id = $requestId" }

        try {
            Request.findById(requestId)?.toRequest() ?: error("Request does not exist")
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }

    override suspend fun getAllRequestsByHotelId(hotelId: Int): List<RequestDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get all requests by hotel id = $hotelId" }

        try {
            Requests.select { Requests.hotel_id eq hotelId }.map { resultRowToRequest(it).toRequest() }
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }

    override suspend fun getAllRequestsByClientId(clientId: Int): List<RequestDTO> = DatabaseFactory.dbQuery {
        logger.debug { "get all requests by clientId id = $clientId" }

        try {
            Requests.select { Requests.from_client_id eq clientId }.map { resultRowToRequest(it).toRequest() }
        } catch (e: Throwable) {
            logger.debug { "${e.message}" }
            error("${e.message}")
        }
    }

    override suspend fun updateRequestStatus(requestId: Int, status: String): Int = DatabaseFactory.dbQuery {
        logger.debug { "update request status $requestId with status $status" }

        val requestStatus =
            RequestStatus.find { RequestStatuses.status eq status }.singleOrNull() ?: error("Incorrect status")

        try {
            Requests.update({ Requests.id eq requestId }) {
                it[request_status_id] = requestStatus.id.value
            }
        } catch (e: Throwable) {
            logger.debug { "Request does not exists" }
            error("Request does not exists")
        }
    }

    override suspend fun updateRequest(requestId: Int, type: String, additionalInfo: String): Int =
        DatabaseFactory.dbQuery {
            logger.debug { "update request $requestId with type $type and additionalInfo $additionalInfo" }

            val requestType = RequestType.find { RequestTypes.type eq type }.singleOrNull() ?: error("Incorrect type")

            val request = Request.findById(requestId) ?: error("Incorrect requestId")

            if (request.statusId.id.value != ERequestStatus.PENDING.databaseId) {
                error("Change request data is possible only in pending status")
            }

            try {
                Requests.update({ Requests.id eq requestId }) {
                    it[request_type_id] = requestType.id.value
                    it[additional_info] = additionalInfo
                }
            } catch (e: Throwable) {
                logger.debug { "Request does not exists" }
                error("Request does not exists")
            }
        }
}

val requestService: RequestService = RequestServiceImpl().apply {}