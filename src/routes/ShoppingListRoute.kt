package com.sigismund.routes

import com.sigismund.domain.data.repositories.IngredientRepository
import com.sigismund.models.Ingredient
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val SHOPPING = "shopping"
const val SHOPPING_CREATE = "$SHOPPING/create"
const val SHOPPING_DELETE = "$SHOPPING/delete"
const val SHOPPING_CHECK = "$SHOPPING/check"
const val SHOPPING_UNCHECK = "$SHOPPING/uncheck"

@KtorExperimentalLocationsAPI
@Location(SHOPPING)
class ShoppingListRoute

@KtorExperimentalLocationsAPI
@Location(SHOPPING_CREATE)
class ShoppingCreate

@KtorExperimentalLocationsAPI
@Location(SHOPPING_DELETE)
class ShoppingDelete

@KtorExperimentalLocationsAPI
@Location(SHOPPING_CHECK)
class ShoppingCheck

@KtorExperimentalLocationsAPI
@Location(SHOPPING_UNCHECK)
class ShoppingUncheck

@KtorExperimentalLocationsAPI
fun Route.shopping(db: IngredientRepository) {

    authenticate("jwt") {

        get<ShoppingListRoute> {
            val userId = call.request.headers["userId"]?.toInt()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }

            try {
                val ingredients = db.getIngredientsInShoppingList(userId)
                call.respond(HttpStatusCode.OK, ingredients)
            } catch (t: Throwable) {
                application.log.error("Failed to get ingredients", t)
                call.respond(HttpStatusCode.BadRequest, "Failed to get shopping list")
            }
        }

        post<ShoppingCreate> {
            val ingredient = call.receive<Ingredient>()
            val userId = call.request.headers["userId"]?.toInt()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
                return@post
            }

            try {
                ingredient.userId = userId
                val id = db.createIngredient(ingredient)
                id?.let {
                    call.respond(HttpStatusCode.OK)
                }
            } catch (t: Throwable) {
                application.log.error("Failed to save ingredient", t)
                call.respond(HttpStatusCode.BadRequest, "Failed to save ingredient to the list")
            }

        }

        delete<ShoppingDelete> {
            val ingredient = call.receive<Ingredient>()
            try {
                ingredient.id.let {
                    db.deleteIngredient(it)
                    call.respond(HttpStatusCode.OK)
                }
            } catch (t: Throwable) {
                application.log.error("Failed to delete Ingredient", t)
                call.respond(HttpStatusCode.BadRequest, "Problems deleting ingredient")
            }
        }

        post<ShoppingCheck> {
            val ingredient = call.receive<Ingredient>()
            try {
                ingredient.id.let {
                    db.chekAsInList(it)
                    call.respond(HttpStatusCode.OK)
                }
            } catch (t: Throwable) {
                application.log.error("Failed to check ingredient", t)
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post<ShoppingUncheck> {
            val ingredient = call.receive<Ingredient >()
            try {
                ingredient.id.let {
                    db.chekAsInList(it)
                    call.respond(HttpStatusCode.OK)
                }
            } catch (t: Throwable) {
                application.log.error("Failed to uncheck ingredient", t)
                call.respond(HttpStatusCode.BadRequest)
            }
        }

    }
}