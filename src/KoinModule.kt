package com.sigismund

import com.sigismund.auth.Auth
import com.sigismund.auth.JwtService
import com.sigismund.data.cookingstep.CookingStepsDataSourceImpl
import com.sigismund.data.ingredient.IngredientDataSourceImpl
import com.sigismund.data.ingredient.IngredientRepositoryImpl
import com.sigismund.data.recipe.RecipeDataSourceImpl
import com.sigismund.data.recipe.RecipeRepositoryImpl
import com.sigismund.data.user.UserDataSourceImpl
import com.sigismund.data.user.UserRepositoryImpl
import com.sigismund.domain.data.repositories.IngredientRepository
import com.sigismund.domain.data.repositories.RecipeRepository
import com.sigismund.domain.data.repositories.UserRepository
import com.sigismund.domain.data.sources.CookingStepDataSource
import com.sigismund.domain.data.sources.IngredientDataSource
import com.sigismund.domain.data.sources.RecipeDataSource
import com.sigismund.domain.data.sources.UserDataSource
import com.sigismund.domain.services.RecipeService
import com.sigismund.domain.services.ShoppingService
import com.sigismund.domain.services.UserService
import io.ktor.util.*
import org.koin.dsl.module

@KtorExperimentalAPI
val appModule = module {
    single { JwtService() }
    single { Auth() }

    single<RecipeDataSource> { RecipeDataSourceImpl() }
    single<IngredientDataSource> { IngredientDataSourceImpl() }
    single<CookingStepDataSource> { CookingStepsDataSourceImpl() }
    single<UserDataSource> { UserDataSourceImpl() }

    single<RecipeRepository> { RecipeRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<IngredientRepository> { IngredientRepositoryImpl(get()) }

    single { RecipeService(get()) }
    single { UserService(get(), get(), get()) }
    single { ShoppingService(get()) }
}