package com.sigismund

import com.sigismund.data.DatabaseFactory
import com.sigismund.data.RecipeRepositoryImpl
import com.sigismund.data.models.Recipe
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }

    DatabaseFactory.init()
    val repo = RecipeRepositoryImpl()

    routing {
        route("/recipes") {

            post {
                val recipe = call.receive<Recipe>()
                try {
                    val id = repo.addRecipe(recipe)
                    id?.let {
                        call.respond(HttpStatusCode.OK, "${it.value}")
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems saving recipe")
                }
            }

            get {
                val recipes = repo.getRecipes()
                call.respond(HttpStatusCode.OK, recipes)
            }

            get("{id}") {
                val id = call.parameters["id"]
                try {
                    id?.toInt()?.let {
                        val recipe = repo.findRecipe(it)
                        call.respond(HttpStatusCode.OK, recipe)
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems getting recipe")
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]
                try {
                    id?.toInt()?.let {
                        repo.deleteRecipe(it)
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems deleting recipe")
                }
            }

            patch("{id}") {
                val recipe = call.receive<Recipe>()
                val id = call.parameters["id"]
                try {
                    id?.toInt()?.let {
                        repo.updateRecipe(recipe, it)
                        call.respond(HttpStatusCode.OK)
                    }
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.BadRequest, "Problems updating recipe")
                }
            }

        }
    }

}

