package com.sigismund

import com.sigismund.data.DatabaseFactory
import com.sigismund.data.RecipeRepositoryImpl
import com.sigismund.data.UserRepositoryImpl
import com.sigismund.auth.JwtService
import com.sigismund.auth.MySession
import com.sigismund.auth.hash
import com.sigismund.routes.recipes
import com.sigismund.routes.users
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    install(Locations) {
    }

    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    DatabaseFactory.init()
    val recipeRepo = RecipeRepositoryImpl()
    val userRepo = UserRepositoryImpl()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }


    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "CookBook Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = userRepo.findUser(claimString)
                user
            }
        }
    }

    routing {

        users(userRepo, jwtService, hashFunction)

        recipes(recipeRepo, userRepo)

    }

}

