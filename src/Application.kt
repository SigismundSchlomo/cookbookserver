package com.sigismund

import com.sigismund.data.DatabaseFactory
import com.sigismund.auth.JwtService
import com.sigismund.domain.data.repositories.UserRepository
import com.sigismund.domain.services.RecipeService
import com.sigismund.domain.services.ShoppingService
import com.sigismund.domain.services.UserService
import com.sigismund.routes.recipes
import com.sigismund.routes.shopping
import com.sigismund.routes.users
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.util.*
import org.koin.experimental.property.inject
import org.koin.ktor.ext.inject

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

    DatabaseFactory.init()


    val jwtService: JwtService by inject()
    val userRepo: UserRepository by inject()
    val recipeService: RecipeService by inject()
    val userService: UserService by inject()
    val shoppingService : ShoppingService by inject()

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

        users(userService)

        recipes(recipeService)

        shopping(shoppingService)

    }

}


