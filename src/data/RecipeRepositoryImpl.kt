package com.sigismund.data

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.models.CookingStep
import com.sigismund.models.Ingredient
import com.sigismund.models.Recipe
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*

class RecipeRepositoryImpl : RecipeRepository {
    override suspend fun addRecipe(recipe: Recipe): EntityID<Int>? {
        var id: EntityID<Int>? = null
        dbQuery {
            id = Recipes.insertAndGetId {
                it[header] = recipe.header
                it[body] = recipe.body
                it[userId] = recipe.userId
            }
        }
        recipe.ingredients.forEach { ingredient ->
            dbQuery {
                Ingredients.insert { ingredients ->
                    ingredients[userId] = ingredient.userId
                    ingredients[recipeId] = ingredient.recipeId
                    ingredients[name] = ingredient.name
                    ingredients[quantity] = ingredient.quantity
                    ingredients[isInTheList] = ingredient.isInTheList
                }
            }
        }
        recipe.cookingSteps.forEach { cookingStep ->
            dbQuery {
                CookingSteps.insert { cookingSteps ->
                    cookingSteps[recipeId] = cookingStep.recipeId
                    cookingSteps[description] = cookingStep.description
                }
            }
        }
        return id
    }

    override suspend fun deleteRecipe(id: Int) {
        dbQuery { Recipes.deleteWhere { Recipes.id eq id } }
        dbQuery { Ingredients.deleteWhere { Ingredients.recipeId eq id } }
        dbQuery { CookingSteps.deleteWhere { CookingSteps.recipeId eq id } }
    }

    override suspend fun getRecipes(userId: Int): List<Recipe> {
        val recipes = dbQuery {
            Recipes.select {
                Recipes.userId.eq(userId)
            }.mapNotNull { rowToRecipe(it) }
        }

        recipes.forEach {recipe ->
            recipe.ingredients = dbQuery {
                Ingredients.select {
                    Ingredients.recipeId.eq(recipe.id)
                }.mapNotNull { row ->
                    rowToIngredient(row)
                }
            }
        }

        recipes.forEach { recipe ->
            recipe.cookingSteps = dbQuery {
                CookingSteps.select {
                    CookingSteps.recipeId.eq(recipe.id)
                }.mapNotNull { row ->
                    rowToCookingStep(row)
                }
            }
        }

        return recipes
    }

    override suspend fun findRecipe(id: Int): Recipe {
        val recipe = dbQuery {
            Recipes.select { Recipes.id eq id }.mapNotNull { rowToRecipe(it) }.first()
        }
        recipe.ingredients = dbQuery {
            Ingredients.select { Ingredients.recipeId eq recipe.id }.mapNotNull { rowToIngredient(it) }
        }
        recipe.cookingSteps = dbQuery {
            CookingSteps.select { CookingSteps.recipeId eq recipe.id }.mapNotNull { rowToCookingStep(it) }
        }
        return recipe
    }

    override suspend fun updateRecipe(recipe: Recipe, id: Int) {
        dbQuery { Recipes.update ({ Recipes.id eq id }) {
            it[header] = recipe.header
            it[body] = recipe.body
        } }

        dbQuery { Ingredients.deleteWhere { Ingredients.recipeId eq id } }
        recipe.ingredients.forEach { ingredient ->
            dbQuery {
                Ingredients.insert { ingredients ->
                    ingredients[userId] = ingredient.userId
                    ingredients[recipeId] = ingredient.recipeId
                    ingredients[name] = ingredient.name
                    ingredients[quantity] = ingredient.quantity
                    ingredients[isInTheList] = ingredient.isInTheList
                }
            }
        }

        dbQuery { CookingSteps.deleteWhere { CookingSteps.recipeId eq id } }
        recipe.cookingSteps.forEach { cookingStep ->
            dbQuery {
                CookingSteps.insert { cookingSteps ->
                    cookingSteps[recipeId] = cookingStep.recipeId
                    cookingSteps[description] = cookingStep.description
                }
            }
        }
    }

    private fun rowToRecipe(row: ResultRow?): Recipe? {
        if (row == null) return null
        return Recipe(
            id = row[Recipes.id].value,
            userId = row[Recipes.userId],
            header = row[Recipes.header],
            body = row[Recipes.body]
        )
    }

    private fun rowToIngredient(row: ResultRow?): Ingredient? {
        if (row == null) return null
        return Ingredient(
            id = row[Ingredients.id].value,
            userId = row[Ingredients.userId],
            recipeId = row[Ingredients.recipeId],
            name = row[Ingredients.name],
            quantity = row[Ingredients.quantity],
            isInTheList = row[Ingredients.isInTheList]
        )
    }

    private fun rowToCookingStep(row: ResultRow?): CookingStep? {
        if (row == null) return null
        return CookingStep(
            recipeId = row[CookingSteps.recipeId],
            description = row[CookingSteps.description]
        )
    }

}