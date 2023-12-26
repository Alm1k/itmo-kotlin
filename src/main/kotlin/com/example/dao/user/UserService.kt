package com.example.dao.user

import com.example.models.User

interface UserService {
    suspend fun getAllUsers(): List<User>
    suspend fun getUser(id: Int): User?
    suspend fun addNewUser(name: String, surname: String, login: String, password: String): User?
    //suspend fun editUser(id: Int, bDay?: String, ): Boolean
}
