package com.sigismund.data.user

import com.sigismund.data.DatabaseFactory
import com.sigismund.domain.data.sources.UserDataSource
import com.sigismund.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class UserDataSourceImpl : UserDataSource {

    override suspend fun addUser(email: String, displayName: String, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = Users.insert {
                it[Users.email] = email
                it[Users.displayName] = displayName
                it[Users.passwordHash] = passwordHash
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun deleteUser(userId: Int) {
        DatabaseFactory.dbQuery {
            Users.deleteWhere {
                Users.id.eq(userId)
            }
        }
    }

    override suspend fun findUser(userId: Int) = DatabaseFactory.dbQuery {
        Users.select { Users.id.eq(userId) }
                .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String) = DatabaseFactory.dbQuery {
        Users.select { Users.email.eq(email) }
                .map { rowToUser(it) }.singleOrNull()
    }


    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
                userId = row[Users.id].value,
                email = row[Users.email],
                displayName = row[Users.displayName],
                passwordHash = row[Users.passwordHash]
        )
    }

}