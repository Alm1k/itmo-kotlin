package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class RequestStatus(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RequestStatus>(RequestStatuses)

    var status by RequestStatuses.status
    val requests by Request referrersOn Requests.request_status_id

    fun toRequestStatus() = RequestStatusDTO(id.value, status)
}

object RequestStatuses : IntIdTable("request_statuses") {
    val status = varchar("status", 20)
}

data class RequestStatusDTO(
    val id: Int,
    val status: String
)
