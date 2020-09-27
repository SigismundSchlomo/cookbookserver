package com.sigismund.routes

import com.sigismund.domain.data.repositories.RecipeRepository
import com.sigismund.models.Recipe
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val RECIPES = "/recipes"

@KtorExperimentalLocationsAPI
@Location(RECIPES)
class RecipeRoute

@KtorExperimentalLocationsAPI
fun Route.recipes(recipeRepo: RecipeRepository) {
    authenticate("jwt") {

        post<RecipeRoute> {
            val recipe = call.receive<Recipe>()
            val userId = call.request.headers["userId"]?.toInt()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
                return@post
            }

            try {
                recipe.userId = userId
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
            val userId = call.request.headers["userId"]?.toInt()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }

            try {
                val recipes = recipeRepo.getRecipes(userId)
                call.respond(HttpStatusCode.OK, recipes)
            } catch (e: Throwable) {
                application.log.error("Failed to get Recipes", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Recipes")
            }

        }

        delete<RecipeRoute> {
            val recipe = call.receive<Recipe>()
            try {
                recipe.id.let {
                    recipeRepo.deleteRecipe(it)
                    call.respond(HttpStatusCode.OK)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to delete Recipe", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting recipe")
            }

        }

    }
}