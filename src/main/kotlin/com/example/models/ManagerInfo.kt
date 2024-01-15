package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class ManagerInfo(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ManagerInfo>(ManagerInfos)

    var manager by User referencedOn ManagerInfos.manager_id
    val rooms by Room referrersOn Rooms.manager_info_id

    fun toManagerInfo() = ManagerInfoDTO(id.value, manager.toUser(), rooms.map{ it.toRoom()})
}

object ManagerInfos : IntIdTable("manager_infos") {
    val manager_id = reference("manager_id", Users.id)
}

data class ManagerInfoDTO(
    val id: Int,
    val manager: UserDTO,
    val rooms: List<RoomDTO>
)
