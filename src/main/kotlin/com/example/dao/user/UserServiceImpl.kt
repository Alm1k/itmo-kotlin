package com.example.dao.user

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.User
import com.example.models.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class UserServiceImpl : UserService {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        name = row[Users.name],
        surname = row[Users.surname],
        bDay = row[Users.bDay],
        login = row[Users.login],
        password = row[Users.password],
        email = row[Users.email] ?: "",
        roomId = row[Users.roomId]?.toInt(),
    )

    override suspend fun getAllUsers(): List<User>  = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun addNewUser(name: String, surname: String, login: String, password: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.name] = name
            it[Users.surname] = surname
            it[Users.password] = password
            it[Users.login] = login
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

//    override suspend fun editUser(id: Int, bDay?: LocalDateTime = LocalDateTime.now(), ): Boolean = dbQuery {
//        Users.update({ Users.id eq id }) {
//            it[Articles.title] = title
//            it[Articles.body] = body
//        } > 0
//    }
}

val service: UserService = UserServiceImpl().apply {
    runBlocking {
        if(getAllUsers().isEmpty()) {
            addNewUser("vasya", "vasiliev", "vasya96", "1234")
        }
    }
}
