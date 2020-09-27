package com.sigismund.domain.services

import com.sigismund.domain.data.repositories.IngredientRepository
import com.sigismund.models.Ingredient

class ShoppingService(private val ingredientRepository: IngredientRepository) {

    suspend fun getShoppingList(userId: Int): List<Ingredient> {
        return ingredientRepository.getIngredientsInShoppingList(userId)
    }

    suspend fun createIngredient(ingredient: Ingredient): Int {
        return ingredientRepository.createIngredient(ingredient)?.value!!
    }

    suspend fun deleteIngredient(ingredientId: Int) {
        ingredientRepository.deleteIngredient(ingredientId)
    }

    suspend fun chekAsInList(ingredientId: Int) {
        ingredientRepository.chekAsInList(ingredientId)
    }

    suspend fun uncheckAsInTheList(ingredientId: Int) {
        ingredientRepository.uncheckAsInTheList(ingredientId)
    }

}