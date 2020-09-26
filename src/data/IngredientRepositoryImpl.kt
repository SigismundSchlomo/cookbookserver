package com.sigismund.data

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.models.Ingredient
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class IngredientRepositoryImpl : IngredientsRepository {

    override suspend fun getIngredientsInShoppingList(userId: Int): List<Ingredient> {
        return dbQuery {
            Ingredients.select {
                Ingredients.userId.eq(userId)
            }.mapNotNull { row ->
                rowToIngredient(row)
            }
        }
    }

    override suspend fun createIngredient(ingredient: Ingredient) {
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

    override suspend fun deleteIngredient(ingredientId: Int) {
        dbQuery { Ingredients.deleteWhere { Ingredients.id eq ingredientId } }
    }

    private fun rowToIngredient(row: ResultRow?): Ingredient? {
        if (row == null) return null
        return Ingredient(
            userId = row[Ingredients.userId],
            recipeId = row[Ingredients.recipeId],
            name = row[Ingredients.name],
            quantity = row[Ingredients.quantity],
            isInTheList = row[Ingredients.isInTheList]
        )
    }
}