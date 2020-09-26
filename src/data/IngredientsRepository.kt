package com.sigismund.data

import com.sigismund.models.Ingredient

interface IngredientsRepository {
    suspend fun getIngredientsInShoppingList(userId: Int): List<Ingredient>
    suspend fun createIngredient(ingredient: Ingredient)
    suspend fun deleteIngredient(ingredientId: Int)
}