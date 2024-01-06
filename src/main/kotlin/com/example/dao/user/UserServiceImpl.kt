package com.example.dao.user

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.ERole
import com.example.models.User
import com.example.models.Users
import com.example.models.Users.login
import com.example.utils.BcryptHasher
import com.example.utils.JwtConfig
import io.ktor.server.auth.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import io.github.oshai.kotlinlogging.KotlinLogging

class UserServiceImpl : UserService {

    private val logger = KotlinLogging.logger {}

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        name = row[Users.name],
        surname = row[Users.surname],
        bDay = row[Users.bDay],
        login = row[Users.login],
        password = row[Users.password],
        email = row[Users.email],
        roleId = row[Users.roleId]
        //roomId = row[Users.roomId]
    )

    override suspend fun getAllUsers(): List<User>  = dbQuery {
        logger.debug { "get all users" }
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun getUser(id: Int): User? = dbQuery {
        logger.debug { "get user by id: $id" }
        Users.select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun addNewUser(name: String, surname: String, roleId: Int, login: String, password: String): User? = dbQuery {
        logger.debug { "add new user" }
        val insertStatement = Users.insert {
            it[Users.name] = name
            it[Users.surname] = surname
            it[Users.password] = password
            it[Users.login] = login
            it[Users.roleId] = roleId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        logger.debug { "delete user with id: $id" }
        Users.deleteWhere { Users.id eq id } > 0
    }

//    override suspend fun isDirector(): Boolean {
//        Users.select { Users.roleId eq ERole.DIRECTOR.databaseId }
//            .map(::resultRowToUser)
//            .singleOrNull()
//    }
//
//    override suspend fun isManager(): Boolean {
//        Users.select { Users.roleId eq ERole.ADMIN.databaseId }
//            .map(::resultRowToUser)
//            .singleOrNull()
//    }

    override suspend fun findUserByLogin(login: String): User? = dbQuery {
        logger.debug { "get user by login: $login" }
        Users.select { Users.login eq login }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun findUserByCredentials(credential: UserPasswordCredential): User? {
        logger.debug { "find user by credentials: $credential" }
        val user = findUserByLogin(credential.name) ?: throw Exception("user not found")
        BcryptHasher.checkPassword(credential.password, user)
        return user
    }
}

val service: UserService = UserServiceImpl().apply {
 //   runBlocking {
//    }
}
