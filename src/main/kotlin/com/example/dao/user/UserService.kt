package com.example.dao.user

import com.example.models.User
import com.example.routes.LoginRequest

interface UserService {
    suspend fun getAllUsers(): List<User>
    suspend fun getUser(id: Int): User?
    suspend fun addNewUser(name: String, surname: String, roleId: Int, login: String, password: String): User?
    suspend fun deleteUser(id: Int): Boolean
    suspend fun findUserByLogin(login: String): User?
    suspend fun findUserByCredentials(credential: LoginRequest): User?
//    suspend fun isManager(): Boolean
//    suspend fun isDirector(): Boolean
}
