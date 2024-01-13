package com.example.dao.managerInfo

import com.example.models.ManagerInfo
import com.example.models.ManagerInfoDTO
import org.jetbrains.exposed.sql.ResultRow

interface ManagerInfoService {
    fun mapManagerInfoFromResultRow(row: ResultRow): ManagerInfo
    suspend fun addManagerInfo(managerId: Int): ManagerInfo?
    suspend fun getManagerInfoByManagerId(id: Int): ManagerInfoDTO?
}
