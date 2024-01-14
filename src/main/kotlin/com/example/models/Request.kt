package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class Request(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Request>(Requests)

    var from_client_id by User referencedOn Requests.from_client_id
    var to_client_id by User referencedOn Requests.to_client_id
    var type by Requests.type
    var status by Requests.status
    var additional_info by Requests.additional_info

    fun toRequest() = RequestDTO(id.value, from_client_id.id.value, to_client_id.id.value, type, status, additional_info)
}

object Requests : IntIdTable("requests") {
    val from_client_id = reference("from_client_id", Users.id)
    val to_client_id = reference("to_client_id", Users.id)
    val type = varchar("type", 50)
    val status = varchar("status", 20)
    val additional_info = varchar("additional_info", 100)
}

data class RequestDTO(
    val id: Int,
    val from_client_id: Int,
    val to_client_id: Int,
    val type: String,
    val status: String,
    val additional_info: String,
)