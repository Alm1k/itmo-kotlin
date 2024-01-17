package com.example.dao.request

import com.example.models.Request
import com.example.models.RequestDTO

interface RequestService {
    suspend fun createRequest(
        fromClientId: Int,
        hotelId: Int,
        roomId: Int,
        type: String,
        additionalInfo: String
    ): Request?

    suspend fun getRequestById(requestId: Int): RequestDTO

    suspend fun getAllRequestsByHotelId(hotelId: Int): List<RequestDTO>

    suspend fun getAllRequestsByClientId(clientId: Int): List<RequestDTO>

    suspend fun updateRequestStatus(requestId: Int, status: String): Int

    suspend fun updateRequest(requestId: Int, type: String, additionalInfo: String): Int
}