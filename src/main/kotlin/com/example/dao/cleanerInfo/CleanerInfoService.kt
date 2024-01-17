package com.example.dao.cleanerInfo

import com.example.models.CleanerInfo
import com.example.models.CleanerInfoDTO

interface CleanerInfoService {
    suspend fun addCleanerInfo(cleanerId: Int, hotelId: Int): CleanerInfo

    suspend fun getCleanerInfoByCleanerId(id: Int): CleanerInfoDTO

    suspend fun getHotelCleanersById(hotelId: Int): List<CleanerInfoDTO>
}
