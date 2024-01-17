package com.example.dao.wsConnections

import com.example.dao.hotel.hotelService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class WSConnectionServiceImpl : WSConnectionsService {
    private val connections = ConcurrentHashMap<Int, WebSocketServerSession>()
    private val logger = KotlinLogging.logger {}
    override suspend fun connect(userId: Int, socket: WebSocketServerSession) {
        logger.debug { "WS Request Connection connect for $userId" }

        if (this.checkConnectionExist(userId)) {
            error("Duplicate member")
        }

        connections[userId] = socket
    }

    override suspend fun disconnect(userId: Int) {
        logger.debug { "WS Request Connection close for $userId" }

        if (this.checkConnectionExist(userId)) {
            connections[userId]!!.close()
            connections.remove(userId)
        } else {
            error("Connection for this user does not exist")
        }
    }

    override suspend fun sendMessage(userId: Int, text: String) {
        logger.debug { "WS Request Connection send message $text for $userId" }

        if (this.checkConnectionExist(userId)) {
            connections[userId]!!.send(text)
        } else {
            error("Connection for this user does not exist")
        }
    }

    override suspend fun checkConnectionExist(userId: Int): Boolean {
        return connections.containsKey(userId)
    }

    override suspend fun sendMessageToAllManagersByHotel(hotelId: Int, message: String) {
        hotelService.getAllHotelManagers(hotelId).map {
            if (this.checkConnectionExist(it.manager.id)) {
                this.sendMessage(
                    it.manager.id,
                    message
                )
            }
        }
    }

}

val wsConnectionsService: WSConnectionsService = WSConnectionServiceImpl().apply {}
