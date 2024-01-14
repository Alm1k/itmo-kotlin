package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class CleanerInfo(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<CleanerInfo>(CleanerInfos)

    var cleanerId by User referencedOn CleanerInfos.cleaner_id
    val hotelId by Hotel referencedOn  CleanerInfos.hotel_id
    val cleaningTasks by Cleaning referrersOn Cleanings.cleaner_id

    fun toCleanerInfo() = CleanerInfoDTO(id.value, cleanerId.id.value, hotelId.id.value, cleaningTasks.map { it.toCleaning() })
}

object CleanerInfos : IntIdTable("cleaner_infos") {
    val cleaner_id = reference("cleaner_id", Users.id)
    val hotel_id = reference("hotel_id", Hotels.id)
}

data class CleanerInfoDTO(
    val id: Int,
    val cleanerId: Int,
    val hotelId: Int,
    val cleaningTasks: List<CleaningDTO>
)