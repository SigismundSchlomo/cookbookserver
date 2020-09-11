package com.sigismund.data

import com.sigismund.data.models.User

interface UserRepository {

    suspend fun addUser(email: String,
                        displayName: String,
                        passwordHash: String): User?
    suspend fun deleteUser(userId: Int)
    suspend fun findUser(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?

}