package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class Request(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Request>(Requests)

    var fromClientId by User referencedOn Requests.from_client_id
    var hotelId by Hotel referencedOn Requests.hotel_id
    var roomId by Room referencedOn Requests.room_id
    var typeId by RequestType referencedOn Requests.request_type_id
    var statusId by RequestStatus referencedOn Requests.request_status_id
    var additionalInfo by Requests.additional_info

    fun toRequest() = RequestDTO(
        id.value,
        fromClientId.id.value,
        hotelId.id.value,
        roomId.id.value,
        typeId.id.value,
        statusId.id.value,
        additionalInfo
    )
}

object Requests : IntIdTable("requests") {
    val from_client_id = reference("from_client_id", Users.id)
    val hotel_id = reference("hotel_id", Hotels.id)
    val room_id = reference("room_id", Rooms.id)
    val request_type_id = reference("request_type_id", RequestTypes.id)
    val request_status_id = reference("request_status_id", RequestStatuses.id)
    val additional_info = varchar("additional_info", 100)
}

data class RequestDTO(
    val id: Int,
    val fromClientId: Int,
    val hotelId: Int,
    val roomId: Int,
    val typeId: Int,
    val statusId: Int,
    val additionalInfo: String,
)