package com.sigismund.data.ingredient

import com.sigismund.data.DatabaseFactory
import com.sigismund.domain.data.sources.IngredientDataSource
import com.sigismund.models.Ingredient
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class IngredientDataSourceImpl : IngredientDataSource {

    override suspend fun getIngredientsInShoppingList(userId: Int): List<Ingredient> {
        return DatabaseFactory.dbQuery {
            Ingredients.select {
                Ingredients.userId.eq(userId)
            }.mapNotNull { row ->
                rowToIngredient(row)
            }
        }
    }

    override suspend fun createIngredient(ingredient: Ingredient): EntityID<Int>? {
        var id: EntityID<Int>? = null
        DatabaseFactory.dbQuery {
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
        DatabaseFactory.dbQuery { Ingredients.deleteWhere { Ingredients.id eq ingredientId } }
    }

    override suspend fun chekAsInList(ingredientId: Int) {
        DatabaseFactory.dbQuery { Ingredients.update({ Ingredients.id eq ingredientId }) { it[isInTheList] = true } }
    }

    override suspend fun uncheckAsInTheList(ingredientId: Int) {
        DatabaseFactory.dbQuery { Ingredients.update({ Ingredients.id eq ingredientId }) { it[isInTheList] = false } }
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
