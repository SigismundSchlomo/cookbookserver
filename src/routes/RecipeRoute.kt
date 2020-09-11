package com.sigismund.routes

import com.sigismund.data.RecipeRepository
import com.sigismund.data.UserRepository
import com.sigismund.data.auth.MySession
import com.sigismund.data.models.Recipe
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val RECIPES = "/recipes"

@KtorExperimentalLocationsAPI
@Location(RECIPES)
class RecipeRoute

@KtorExperimentalLocationsAPI
fun Route.recipes(recipeRepo: RecipeRepository, userRepo: UserRepository) {
    authenticate("jwt") {

        post<RecipeRoute> {
            val recipe = call.receive<Recipe>()

            val user = call.sessions.get<MySession>()?.let { userRepo.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
                return@post
            }

            try {
                recipe.userId = user.userId
                val id = recipeRepo.addRecipe(recipe)
                id?.let {
                    call.respond(HttpStatusCode.OK, "${it.value}")
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems saving recipe")
            }
        }

        get<RecipeRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepo.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }

            try {
                val recipes = recipeRepo.getRecipes(user.userId)
                call.respond(HttpStatusCode.OK, recipes)
            } catch (e: Throwable) {
                application.log.error("Failed to get Recipes", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Recipes")
            }

        }

        delete<RecipeRoute> {
            val parameters = call.receive<Parameters>()
            if (!parameters.contains("id")) {
                return@delete call.respond(HttpStatusCode.BadRequest, "Missing Todo Id")
            }
            try {
                parameters["id"]?.toInt()?.let {
                    val recipe = recipeRepo.findRecipe(it)
                    call.respond(HttpStatusCode.OK, recipe)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to delete Recipes", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting recipe")
            }

        }

    }
}