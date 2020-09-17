package com.sigismund.routes

import com.sigismund.auth.JwtService
import com.sigismund.auth.MySession
import com.sigismund.data.UserRepository
import com.sigismund.routes.routingmodels.AuthRequest
import com.sigismund.routes.routingmodels.AuthResponse

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val USERS = "/users"
const val USER_LOGIN = "$USERS/login"
const val USER_LOGOUT = "$USERS/logout"
const val USER_CREATE = "$USERS/create"
const val USER_DELETE = "$USERS/delete"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_LOGOUT)
class UserLogoutRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
@Location(USER_DELETE)
class UserDeleteRoute

@KtorExperimentalLocationsAPI
fun Route.users(db: UserRepository, jwtService: JwtService, hashFunction: (String) -> String) {

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

        val hash = hashFunction(password)

        try {
            val currentUser = db.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hash) {
                    call.sessions.set(MySession(it))
                    val token = jwtService.generateToken(currentUser)
                    val response = AuthResponse(token, currentUser.userId, currentUser.displayName, currentUser.email)
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
        }
    }


//    post<UserLogoutRoute> {
//        val signinParameters = call.receive<Parameters>()
//        val email = signinParameters["email"] ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
//
//        try {
//            val currentUser = db.findUserByEmail(email)
//            currentUser?.userId?.let {
//                call.sessions.clear(call.sessions.findName(MySession::class))
//                call.respond(HttpStatusCode.OK)
//            }
//        } catch (e: Throwable) {
//            application.log.error("Failed to register user", e)
//            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
//        }
//
//    }

    delete<UserDeleteRoute> {
        val request = call.receive<AuthRequest>()
        val email = request.email
        if (email.isEmpty()) {
            return@delete call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        }

        try {
            val currentUser = db.findUserByEmail(email)
            currentUser?.userId?.let {
                db.deleteUser(it)
                call.sessions.clear(call.sessions.findName(MySession::class))
                call.respond(HttpStatusCode.OK)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
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

        val hash = hashFunction(password)

        try {
            val newUser = db.addUser(email, displayName, hash)
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                val token = jwtService.generateToken(newUser)
                val response = AuthResponse(token, newUser.userId, displayName, email)
                call.respond(HttpStatusCode.Created, response)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }

    }

}