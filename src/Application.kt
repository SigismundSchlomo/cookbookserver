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
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.SLF4JLogger

fun main(args: Array<String>){
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

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

    install(Koin) {
        SLF4JLogger()
        modules(appModule)
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


