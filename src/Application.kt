package com.sigismund

import com.sigismund.data.DatabaseFactory
import com.sigismund.data.RecipeRepositoryImpl
import com.sigismund.data.UserRepositoryImpl
import com.sigismund.data.auth.JwtService
import com.sigismund.data.auth.MySession
import com.sigismund.data.auth.hash
import com.sigismund.data.models.Recipe
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.request.*
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
        route("/recipes") {

            post {//Add "/new" to make endpoint more clear
                val recipe = call.receive<Recipe>()
                try {
                    val id = recipeRepo.addRecipe(recipe)
                    id?.let {
                        call.respond(HttpStatusCode.OK, "${it.value}")
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems saving recipe")
                }
            }

            get {
                val recipes = recipeRepo.getRecipes()
                call.respond(HttpStatusCode.OK, recipes)
            }

            get("{id}") {
                val id = call.parameters["id"]
                try {
                    id?.toInt()?.let {
                        val recipe = recipeRepo.findRecipe(it)
                        call.respond(HttpStatusCode.OK, recipe)
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems getting recipe")
                }
            }

            delete("{id}") {  //Change to "/delete/{id}
                val id = call.parameters["id"]
                try {
                    id?.toInt()?.let {
                        recipeRepo.deleteRecipe(it)
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems deleting recipe")
                }
            }

            patch("{id}") {  //Change to "/update/{id}
                val recipe = call.receive<Recipe>()
                val id = call.parameters["id"]
                try {
                    id?.toInt()?.let {
                        recipeRepo.updateRecipe(recipe, it)
                        call.respond(HttpStatusCode.OK)
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems updating recipe")
                }
            }

        }
    }

}

