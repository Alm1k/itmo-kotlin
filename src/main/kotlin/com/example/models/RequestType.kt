package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class RequestType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RequestType>(RequestTypes)

    var type by RequestTypes.type
    val requests by Request referrersOn Requests.request_type_id

    fun toRequestType() = RequestTypeDTO(id.value, type)
}

object RequestTypes : IntIdTable("request_types") {
    val type = varchar("type", 20)
}

data class RequestTypeDTO(
    val id: Int,
    val type: String
)
