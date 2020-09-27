package com.sigismund.domain.data.sources

import com.sigismund.models.Recipe
import org.jetbrains.exposed.dao.id.EntityID

interface RecipeDataSource {

    suspend fun addRecipe(recipe: Recipe): EntityID<Int>?
    suspend fun deleteRecipe(id: Int)
    suspend fun getRecipes(userId: Int): List<Recipe>
    suspend fun findRecipe(id: Int): Recipe
    suspend fun updateRecipe(recipe: Recipe, id: Int)

}