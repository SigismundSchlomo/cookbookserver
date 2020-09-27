package com.sigismund.data.ingredient

import com.sigismund.domain.data.repositories.IngredientRepository
import com.sigismund.domain.data.sources.IngredientDataSource
import com.sigismund.models.Ingredient
import org.jetbrains.exposed.dao.id.EntityID

class IngredientRepositoryImpl(private val ingredientDataSource: IngredientDataSource) : IngredientRepository {

    override suspend fun getIngredientsInShoppingList(userId: Int): List<Ingredient> {
        return ingredientDataSource.getIngredientsInShoppingList(userId)
    }

    override suspend fun createIngredient(ingredient: Ingredient): EntityID<Int>? {
        return ingredientDataSource.createIngredient(ingredient)
    }

    override suspend fun deleteIngredient(ingredientId: Int) {
        return ingredientDataSource.deleteIngredient(ingredientId)
    }

    override suspend fun chekAsInList(ingredientId: Int) {
        return ingredientDataSource.chekAsInList(ingredientId)
    }

    override suspend fun uncheckAsInTheList(ingredientId: Int) {
        return ingredientDataSource.chekAsInList(ingredientId)
    }
}