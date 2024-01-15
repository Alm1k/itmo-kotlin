package com.example.dao.user

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.User
import com.example.models.UserDTO
import com.example.models.Users
import com.example.routes.LoginRequest
import com.example.utils.BcryptHasher
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserServiceImpl : UserService {

    private val logger = KotlinLogging.logger {}

    override suspend fun mapUserFromResultRow(row: ResultRow): User {
        return User.findById(row[Users.id]) ?: error("User not found")
    }

    override suspend fun getAllUsers(): List<UserDTO>  = withContext(Dispatchers.IO){
        logger.debug { "get all users" }
        return@withContext dbQuery {  Users.selectAll().map{ mapUserFromResultRow(it).toUser() } }
    }

    override suspend fun getUser(id: Int): UserDTO? = dbQuery {
        logger.debug { "get user by id: $id" }
        Users.select { Users.id eq id }
            .map { mapUserFromResultRow(it).toUser() }
            .singleOrNull()
    }

    override suspend fun addNewUser(name: String, surname: String, roleId: Int, login: String, password: String): UserDTO? = dbQuery {
        logger.debug { "add new user : name: $name surname: $surname login: $login roleId: $roleId password: $password" }
        val insertStatement = Users.insert {
            it[Users.name] = name
            it[Users.surname] = surname
            it[Users.password] = password
            it[Users.login] = login
            it[Users.role_id] = roleId
        }
        try {
            insertStatement.resultedValues?.singleOrNull()?.let {
                mapUserFromResultRow(it).toUser()
            }
        }
        catch (e: Throwable) {
            logger.debug { "user with such login already exists" }
            null
        }
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        logger.debug { "delete user with id: $id" }
        Users.deleteWhere { Users.id eq id } > 0
    }

    override suspend fun findUserByLogin(login: String): User? = dbQuery {
        logger.debug { "get user by login: $login" }
        Users.select { Users.login eq login }
            .map { mapUserFromResultRow(it) }
            .singleOrNull()
    }

    override suspend fun findUserByCredentials(credential: LoginRequest): User {
        logger.debug { "find user by credentials: $credential" }
        val user = findUserByLogin(credential.login) ?: throw Exception("user not found")
        BcryptHasher.checkPassword(credential.password, user)
        return user
    }

    override suspend fun updateUser(userId: Int, bDay: String, email: String): Int = dbQuery {
        logger.debug { "update user with id: $userId - set bDay: $bDay, email: $email" }
        try {
            Users.update({ Users.id eq userId }) {
                it[Users.bDay] = bDay
                it[Users.email] = email
            }
        } catch (e: Throwable) {
            logger.debug { "user does not exists" }
            error("user does not exists")
        }
    }
}

val userService: UserService = UserServiceImpl().apply {
}
