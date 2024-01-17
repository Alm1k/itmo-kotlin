package com.example.dao.cleaning

import com.example.models.Cleaning
import com.example.models.CleaningDTO

interface CleaningService {
    suspend fun createCleaning(cleanerId: Int, roomId: Int, hotelId: Int): Cleaning?

    suspend fun getCleaningsByCleaner(cleanerId: Int): List<CleaningDTO>

    suspend fun getCleaningsByHotel(hotelId: Int): List<CleaningDTO>

    suspend fun completeCleaning(cleaningId: Int): Int?

    suspend fun getCleaningsById(cleaningId: Int): CleaningDTO?
}