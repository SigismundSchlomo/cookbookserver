package com.sigismund.models

data class Recipe(
    val id: Int = -1,
    var userId: Int = -1,
    val header: String,
    val body: String,
    var ingredients: List<Ingredient> = listOf(),
    var cookingSteps: List<CookingStep> = listOf()
)