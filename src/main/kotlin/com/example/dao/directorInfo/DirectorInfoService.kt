package com.example.dao.directorInfo

import com.example.models.DirectorInfo
import com.example.models.DirectorInfoDTO

interface DirectorInfoService {
    suspend fun addDirectorInfo(directorId: Int): DirectorInfo?

    suspend fun getDirectorInfoByDirectorId(id: Int): DirectorInfoDTO?
}
