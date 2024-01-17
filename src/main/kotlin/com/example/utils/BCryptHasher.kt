package com.example.utils

import com.example.models.ApiError
import com.example.models.User
import io.ktor.http.*
import org.mindrot.jbcrypt.BCrypt

object BcryptHasher {

    fun checkPassword(attempt: String, user: User) = if (BCrypt.checkpw(attempt, user.password)) Unit
        else throw ApiError(HttpStatusCode.InternalServerError, "wrong password")

    fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

}
