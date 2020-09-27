package com.sigismund.data.user

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.domain.data.repositories.UserRepository
import com.sigismund.domain.data.sources.UserDataSource
import com.sigismund.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class UserRepositoryImpl(private val userDataSource: UserDataSource) : UserRepository {

    override suspend fun addUser(email: String, displayName: String, passwordHash: String): User? {
        return userDataSource.addUser(email, displayName, passwordHash)
    }

    override suspend fun deleteUser(userId: Int) {
        userDataSource.deleteUser(userId)
    }

    override suspend fun findUser(userId: Int): User? {
        return userDataSource.findUser(userId)
    }

    override suspend fun findUserByEmail(email: String): User? {
        return userDataSource.findUserByEmail(email)
    }
}