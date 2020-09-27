package com.sigismund.data

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.models.Ingredient
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*

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
        dbQuery { Ingredients.update ({ Ingredients.id eq ingredientId }) { it[isInTheList] = true } }
    }

    override suspend fun uncheckAsInTheList(ingredientId: Int) {
        dbQuery { Ingredients.update ({Ingredients.id eq ingredientId}) { it[isInTheList] = false } }
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