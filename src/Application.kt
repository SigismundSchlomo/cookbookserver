package com.sigismund

import com.sigismund.auth.Auth
import com.sigismund.data.DatabaseFactory
import com.sigismund.data.recipe.RecipeRepositoryImpl
import com.sigismund.data.user.UserRepositoryImpl
import com.sigismund.auth.JwtService
import com.sigismund.data.cookingstep.CookingStepsDataSourceImpl
import com.sigismund.data.ingredient.IngredientDataSourceImpl
import com.sigismund.data.recipe.RecipeDataSourceImpl
import com.sigismund.data.user.UserDataSourceImpl
import com.sigismund.domain.services.RecipeService
import com.sigismund.domain.services.UserService
import com.sigismund.routes.recipes
import com.sigismund.routes.users
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.routing.*
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

    DatabaseFactory.init()

    val recipeDataSource = RecipeDataSourceImpl()
    val ingredientDataSource = IngredientDataSourceImpl()
    val cookingStepDataSource = CookingStepsDataSourceImpl()
    val recipeRepo = RecipeRepositoryImpl(recipeDataSource, ingredientDataSource, cookingStepDataSource)
    val recipeService = RecipeService(recipeRepo)

    val userDataSource = UserDataSourceImpl()
    val userRepo = UserRepositoryImpl(userDataSource)
    val jwtService = JwtService()

    val userService = UserService(userRepo, jwtService, Auth())

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

    }

}

