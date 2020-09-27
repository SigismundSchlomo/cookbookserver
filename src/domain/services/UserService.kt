package com.sigismund.domain.services

import com.sigismund.auth.Auth
import com.sigismund.auth.JwtService
import com.sigismund.domain.data.repositories.UserRepository
import com.sigismund.domain.services.routingmodels.AuthResponse
import io.ktor.util.*

@KtorExperimentalAPI
class UserService(private val userRepository: UserRepository,
        private val jwtService: JwtService,
        private val auth: Auth) {

    suspend fun loginUser(email: String, password: String): AuthResponse {
        val user = userRepository.findUserByEmail(email)
            ?: throw NoSuchUserException("User doesn't exist")

        val hash = auth.hash(password)

        return if (user.passwordHash == hash) {
            val token = jwtService.generateToken(user)
            AuthResponse(token, user.userId, user.displayName, user.email)
        } else throw WrongPasswordException("Wrong password")

    }

    suspend fun deleteUser(email: String, password: String) {
        val user = userRepository.findUserByEmail(email)
            ?: throw NoSuchUserException("User doesn't exist")

        val hash = auth.hash(password)
        if (user.passwordHash == hash) {
            userRepository.deleteUser(user.userId)
        }
    }

    suspend fun createUser(email: String, password: String, displayName: String): AuthResponse {
        val hash = auth.hash(password)
        val user = userRepository.addUser(email, displayName, hash)
        val token = jwtService.generateToken(user!!)
        return AuthResponse(token, user.userId, displayName, email)
    }

}

class NoSuchUserException(message: String) : Exception(message)

class WrongPasswordException(message: String) : Exception(message)