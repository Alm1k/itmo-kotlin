package com.example.dao.wsConnections

import io.ktor.server.websocket.*

interface WSConnectionsService {
    suspend fun connect(userId: Int, socket: WebSocketServerSession)
    suspend fun disconnect(userId: Int)
    suspend fun sendMessage(userId: Int, text: String)

    suspend fun checkConnectionExist(userId: Int): Boolean

    suspend fun sendMessageToAllManagersByHotel(hotelId: Int, message: String)
}