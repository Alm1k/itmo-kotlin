package com.example.dao.user

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.ApiError
import com.example.models.User
import com.example.models.UserDTO
import com.example.models.Users
import com.example.routes.LoginRequest
import com.example.utils.BcryptHasher
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserServiceImpl : UserService {

    private val logger = KotlinLogging.logger {}

    override suspend fun mapUserFromResultRow(row: ResultRow): User {
        logger.debug { "$row" }
        return User.findById(row[Users.id])
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
    }

    override suspend fun getAllUsers(): List<UserDTO>  = withContext(Dispatchers.IO){
        logger.debug { "get all users" }
        return@withContext dbQuery {  Users.selectAll().map{ mapUserFromResultRow(it).toUser() } }
    }

    override suspend fun getUser(id: Int?): UserDTO = dbQuery {
        logger.debug { "get user by id: $id" }
        if (id != null) {
            Users.select { Users.id eq id }
                .map { mapUserFromResultRow(it).toUser() }
                .singleOrNull()
                ?: throw ApiError(HttpStatusCode.NotFound, "user with id $id not found")
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "user id is invalid")
        }
    }

    override suspend fun addUser(name: String, surname: String, roleId: Int, login: String, password: String): UserDTO? = dbQuery {
        logger.debug { "add new user : name: $name surname: $surname login: $login roleId: $roleId password: $password" }
        if (doesLoginExist(login)){
            throw ApiError(HttpStatusCode.Conflict, "user with login $login already exists")
        }
        val insertStatement = Users.insert {
            it[Users.name] = name
            it[Users.surname] = surname
            it[Users.password] = password
            it[Users.login] = login
            it[role_id] = roleId
        }
        val user = insertStatement.resultedValues?.singleOrNull()
            ?: throw ApiError(HttpStatusCode.InternalServerError, "Internal Server Error" )
        mapUserFromResultRow(user).toUser()
    }

    override suspend fun deleteUser(id: Int?): Boolean = dbQuery {
        logger.debug { "delete user with id: $id" }
        if (id != null) {
            val res = Users.deleteWhere { Users.id eq id }
            when {
                res > 0 -> return@dbQuery true
                else -> throw ApiError(HttpStatusCode.NotFound, "user with id $id not found")
            }
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "user id is invalid")
        }
    }

    override suspend fun findUserByLogin(login: String): User = dbQuery {
        logger.debug { "get user by login: $login" }
            Users.select { Users.login eq login }
                .map { mapUserFromResultRow(it) }
                .singleOrNull()
                ?: throw ApiError(HttpStatusCode.NotFound, "user with login $login not found")
    }

    override suspend fun getUserDTOByLogin(login: String): UserDTO = dbQuery {
        logger.debug { "get userDTO by login: $login" }
        Users.select { Users.login eq login }
            .map { mapUserFromResultRow(it) }
            .singleOrNull()?.toUser()
            ?: throw ApiError(HttpStatusCode.NotFound, "user with login $login not found")
    }

    private suspend fun doesLoginExist(login: String): Boolean = dbQuery {
        logger.debug { "get user by login: $login" }
        val user = Users.select { Users.login eq login }
            .map { mapUserFromResultRow(it) }.singleOrNull()
        when {
            user !== null -> return@dbQuery true
            else -> return@dbQuery false
        }
    }

    override suspend fun findUserByCredentials(credential: LoginRequest): User {
        logger.debug { "find user by credentials: $credential" }
        val user = findUserByLogin(credential.login)
        BcryptHasher.checkPassword(credential.password, user)
        return user
    }

    override suspend fun updateUser(userId: Int?, bDay: String, email: String): Boolean = dbQuery {
        logger.debug { "update user with id: $userId - set bDay: $bDay, email: $email" }
        if (userId != null) {
            val res = Users.update({ Users.id eq userId }) {
                it[Users.bDay] = bDay
                it[Users.email] = email
            }
            when {
                res > 0 -> return@dbQuery true
                else -> throw ApiError(HttpStatusCode.NotFound, "user with id $userId not found")
            }
        } else {
            throw ApiError(HttpStatusCode.BadRequest, "user id is invalid")
        }
    }
}

val userService: UserService = UserServiceImpl().apply {
}
