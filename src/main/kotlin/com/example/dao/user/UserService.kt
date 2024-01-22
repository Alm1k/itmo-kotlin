package com.example.dao.user

import com.example.models.User
import com.example.models.UserDTO
import com.example.routes.LoginRequest
import org.jetbrains.exposed.sql.ResultRow

interface UserService {
    suspend fun mapUserFromResultRow(row: ResultRow): User
    suspend fun getAllUsers(): List<UserDTO>
    suspend fun getUser(id: Int?): UserDTO
    suspend fun addUser(name: String, surname: String, roleId: Int,
                                login: String, password: String): UserDTO?
    suspend fun deleteUser(id: Int?): Boolean
    suspend fun findUserByLogin(login: String): User
    suspend fun getUserDTOByLogin(login: String): UserDTO
    suspend fun findUserByCredentials(credential: LoginRequest): User
    suspend fun updateUser(userId: Int?, bDay: String, email: String): Boolean
}
