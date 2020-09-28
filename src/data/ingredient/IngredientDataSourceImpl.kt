package com.sigismund.data.ingredient

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.domain.data.sources.IngredientDataSource
import com.sigismund.models.Ingredient
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*

class IngredientDataSourceImpl : IngredientDataSource {

    override suspend fun getIngredients(recipeId: Int): List<Ingredient> {
        return dbQuery {
            Ingredients.select {
                Ingredients.recipeId eq recipeId
            }.mapNotNull { row ->
                rowToIngredient(row)
            }
        }
    }

    override suspend fun deleteIngredients(recipeId: Int) {
        dbQuery { Ingredients.deleteWhere { Ingredients.recipeId eq recipeId } }
    }

    override suspend fun insertIngredients(ingredients: List<Ingredient>) {
        if (ingredients.isNullOrEmpty()) return
        ingredients.forEach { ingredient ->
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
    }

    override suspend fun updateIngredients(ingredients: List<Ingredient>, recipeId: Int) {
        ingredients.forEach {ingredient ->
            dbQuery {
                Ingredients.update ({Ingredients.recipeId eq recipeId}) {ingredients ->
                    ingredients[name] = ingredient.name
                    ingredients[quantity] = ingredient.quantity
                    ingredients[isInTheList] = ingredient.isInTheList
                }
            }

        }
    }

    override suspend fun getIngredientsInShoppingList(userId: Int): List<Ingredient> {
        return dbQuery {
            Ingredients.select {
                Ingredients.userId.eq(userId)
            }.mapNotNull { row ->
                rowToIngredient(row)
            }
        }
    }

    override suspend fun createIngredient(ingredient: Ingredient): EntityID<Int>? {
        var id: EntityID<Int>? = null
        dbQuery {
            id = Ingredients.insertAndGetId { ingredients ->
                ingredients[userId] = ingredient.userId
                ingredients[recipeId] = ingredient.recipeId
                ingredients[name] = ingredient.name
                ingredients[quantity] = ingredient.quantity
                ingredients[isInTheList] = ingredient.isInTheList
            }
        }
        return id
    }

    override suspend fun deleteIngredient(ingredientId: Int) {
        dbQuery { Ingredients.deleteWhere { Ingredients.id eq ingredientId } }
    }

    override suspend fun chekAsInList(ingredientId: Int) {
        dbQuery { Ingredients.update({ Ingredients.id eq ingredientId }) { it[isInTheList] = true } }
    }

    override suspend fun uncheckAsInTheList(ingredientId: Int) {
        dbQuery { Ingredients.update({ Ingredients.id eq ingredientId }) { it[isInTheList] = false } }
    }

    private fun rowToIngredient(row: ResultRow?): Ingredient? {
        if (row == null) return null
        if (!row[Ingredients.isInTheList]) return null
        return Ingredient(
                id = row[Ingredients.id].value,
                userId = row[Ingredients.userId],
                recipeId = row[Ingredients.recipeId],
                name = row[Ingredients.name],
                quantity = row[Ingredients.quantity],
                isInTheList = row[Ingredients.isInTheList]
        )
    }

}
