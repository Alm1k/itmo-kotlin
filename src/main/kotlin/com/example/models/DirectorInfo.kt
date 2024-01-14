package com.example.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


class DirectorInfo(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<DirectorInfo>(DirectorInfos)

    var director by User referencedOn DirectorInfos.director_id
    val hotels by Hotel referrersOn Hotels.director_info_id

    fun toDirectorInfo() = DirectorInfoDTO(id.value, director.id.value, hotels.map{ it.toHotel()})
}

object DirectorInfos : IntIdTable("director_infos") {
    val director_id = reference("director_id", Users.id)
}

data class DirectorInfoDTO(
    val id: Int,
    val user: Int,
    val hotels: List<HotelDTO>
)