package com.sigismund.domain.services

import com.sigismund.domain.data.repositories.RecipeRepository
import com.sigismund.models.Recipe

class RecipeService(private val recipeRepository: RecipeRepository) {

    suspend fun createRecipe(recipe: Recipe): Int {
        return recipeRepository.addRecipe(recipe)?.value!!
    }

    suspend fun getRecipes(userId: Int): List<Recipe> {
        return recipeRepository.getRecipes(userId)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeRepository.deleteRecipe(recipe)
    }

}