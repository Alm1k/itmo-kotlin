package com.example.utils

import com.example.models.User
import org.mindrot.jbcrypt.BCrypt

object BcryptHasher {

    fun checkPassword(attempt: String, user: User) = if (BCrypt.checkpw(attempt, user.password)) Unit
    else throw Exception("Wrong Password")

    fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

}
