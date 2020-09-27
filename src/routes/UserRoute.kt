package com.sigismund.routes

import com.sigismund.auth.JwtService
import com.sigismund.domain.data.repositories.UserRepository
import com.sigismund.domain.services.NoSuchUserException
import com.sigismund.domain.services.UserService
import com.sigismund.domain.services.WrongPasswordException
import com.sigismund.domain.services.routingmodels.AuthRequest
import com.sigismund.domain.services.routingmodels.AuthResponse

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

const val USERS = "/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"
const val USER_DELETE = "$USERS/delete"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
@Location(USER_DELETE)
class UserDeleteRoute

@KtorExperimentalLocationsAPI
@KtorExperimentalAPI
fun Route.users(userService: UserService) {

    post<UserLoginRoute> {
        val request = call.receive<AuthRequest>()

        val password = request.password
        if (password.isEmpty()){
            return@post call.respond(HttpStatusCode.Unauthorized, "Missing password")
        }

        val email = request.email
        if (email.isEmpty()) {
            return@post call.respond(HttpStatusCode.Unauthorized, "Missing email")
        }

        try {
            val response = userService.loginUser(email, password)
            call.respond(HttpStatusCode.OK, response)
        } catch (e: NoSuchUserException) {
            call.respond(HttpStatusCode.Unauthorized, e.message.toString())
        } catch (e: WrongPasswordException) {
            call.respond(HttpStatusCode.Unauthorized, e.message.toString())
        } catch (t: Throwable) {
            application.log.error("Failed to login user", t)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
        }
    }

    delete<UserDeleteRoute> {
        val request = call.receive<AuthRequest>()

        val password = request.password
        if (password.isEmpty()){
            return@delete call.respond(HttpStatusCode.Unauthorized, "Missing password")
        }

        val email = request.email
        if (email.isEmpty()) {
            return@delete call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        }

        try {
            userService.deleteUser(email, password)
        } catch (t: Throwable) {
            application.log.error("Failed to delete user", t)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }

    }

    post<UserCreateRoute> {
        val request = call.receive<AuthRequest>()
        val displayName = request.name
        if (displayName.isEmpty()) {
            return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        }

        val password = request.password
        if (password.isEmpty()){
            return@post call.respond(HttpStatusCode.Unauthorized, "Missing password")
        }

        val email = request.email
        if (email.isEmpty()) {
            return@post call.respond(HttpStatusCode.Unauthorized, "Missing email")
        }

        try {
            userService.createUser(email, password, displayName)
        } catch (t: Throwable) {
            application.log.error("Failed to register user", t)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }

    }

}