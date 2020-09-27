package com.sigismund.domain.data.sources

import com.sigismund.models.CookingStep

interface CookingStepDataSource {

    suspend fun getCookingSteps(recipeId: Int): List<CookingStep>
    suspend fun deleteCookingSteps(recipeId: Int)
    suspend fun insertCookingSteps(cookingSteps: List<CookingStep>)
    suspend fun updateCookingSteps(cookingSteps: List<CookingStep>, recipeId: Int)

}