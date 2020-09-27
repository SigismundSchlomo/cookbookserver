package com.sigismund.domain.data.repositories

import com.sigismund.models.Ingredient
import org.jetbrains.exposed.dao.id.EntityID

interface IngredientRepository {

    suspend fun getIngredientsInShoppingList(userId: Int): List<Ingredient>
    suspend fun createIngredient(ingredient: Ingredient): EntityID<Int>?
    suspend fun deleteIngredient(ingredientId: Int)
    suspend fun chekAsInList(ingredientId: Int)
    suspend fun uncheckAsInTheList(ingredientId: Int)

}