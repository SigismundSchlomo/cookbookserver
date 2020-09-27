package com.sigismund.domain.data.sources

import com.sigismund.models.Ingredient
import org.jetbrains.exposed.dao.id.EntityID

interface IngredientDataSource {

    suspend fun getIngredientsInShoppingList(userId: Int): List<Ingredient>
    suspend fun createIngredient(ingredient: Ingredient): EntityID<Int>?
    suspend fun deleteIngredient(ingredientId: Int)
    suspend fun chekAsInList(ingredientId: Int)
    suspend fun uncheckAsInTheList(ingredientId: Int)

}