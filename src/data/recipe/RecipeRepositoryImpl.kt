package com.sigismund.data.recipe

import com.sigismund.domain.data.repositories.RecipeRepository
import com.sigismund.domain.data.sources.CookingStepDataSource
import com.sigismund.domain.data.sources.IngredientDataSource
import com.sigismund.domain.data.sources.RecipeDataSource
import com.sigismund.models.Recipe
import org.jetbrains.exposed.dao.id.EntityID

class RecipeRepositoryImpl(
        private val recipeDataSource: RecipeDataSource,
        private val ingredientDataSource: IngredientDataSource,
        private val cookingStepDataSource: CookingStepDataSource
) : RecipeRepository {

    override suspend fun addRecipe(recipe: Recipe): EntityID<Int>? {
        val id = recipeDataSource.addRecipe(recipe)
        ingredientDataSource.insertIngredients(recipe.ingredients)
        cookingStepDataSource.insertCookingSteps(recipe.cookingSteps)
        return id
    }

    override suspend fun deleteRecipe(id: Int) {
        recipeDataSource.deleteRecipe(id)
        ingredientDataSource.deleteIngredients(id)
        cookingStepDataSource.deleteCookingSteps(id)
    }

    override suspend fun getRecipes(userId: Int): List<Recipe> {
        val recipes = recipeDataSource.getRecipes(userId)

        recipes.forEach {recipe ->
            if (!recipe.ingredients.isNullOrEmpty())
            recipe.ingredients = ingredientDataSource.getIngredients(recipe.id)
        }

        recipes.forEach { recipe ->
            if (!recipe.cookingSteps.isNullOrEmpty()){
                recipe.cookingSteps = cookingStepDataSource.getCookingSteps(recipe.id)
            }
        }

        return recipes
    }

    override suspend fun findRecipe(id: Int): Recipe {
        val recipe = recipeDataSource.findRecipe(id)
        recipe.ingredients = ingredientDataSource.getIngredients(id)
        recipe.cookingSteps = cookingStepDataSource.getCookingSteps(id)
        return recipe
    }

    override suspend fun updateRecipe(recipe: Recipe, id: Int) {
        recipeDataSource.updateRecipe(recipe, id)

        ingredientDataSource.updateIngredients(recipe.ingredients, id)

        cookingStepDataSource.updateCookingSteps(recipe.cookingSteps, id)
    }

}