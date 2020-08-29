package com.sigismund.data

import com.sigismund.data.models.Recipe
import org.jetbrains.exposed.dao.id.EntityID

interface RecipeRepository {

    suspend fun addRecipe(header: String, body: String): EntityID<Int>
    suspend fun deleteRecipe(id: Int)
    suspend fun getRecipes(): List<Recipe>
    suspend fun findRecipe(id: Int): Recipe
    suspend fun updateRecipe(id: Int, header: String, body: String)

}