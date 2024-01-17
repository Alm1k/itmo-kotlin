package com.example.dao.hotelRating

import com.example.models.HotelRating
import com.example.models.HotelRatingDTO

interface HotelRatingService {
    suspend fun addHotelRating(rate: Int, userId: Int, hotelId: Int): HotelRating

    suspend fun getUserRatings(userId: Int): List<HotelRatingDTO>

    suspend fun updateHotelRating(ratingId: Int, rate: Int, userId: Int, hotelId: Int): Int
}