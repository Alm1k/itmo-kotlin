package com.example.dao.hotel

import com.example.models.Hotel
import com.example.models.HotelDTO
import com.example.models.ManagerInfoDTO

interface HotelService {
    suspend fun addHotel(name: String, stageCount: Int, directorId: Int): Hotel

    suspend fun changeDirector(hotelId: Int, directorId: Int): Int

    suspend fun getHotel(hotelId: Int): HotelDTO

    suspend fun getAllHotels(): List<HotelDTO>

    suspend fun updateHotel(hotelId: Int, name: String, stageCount: Int): Int

    suspend fun getAllHotelManagers(hotelId: Int): List<ManagerInfoDTO>
}
