package com.sigismund.domain.data.sources

import com.sigismund.models.User

interface UserDataSource {

    suspend fun addUser(email: String,
                        displayName: String,
                        passwordHash: String): User?
    suspend fun deleteUser(userId: Int)
    suspend fun findUser(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?

}