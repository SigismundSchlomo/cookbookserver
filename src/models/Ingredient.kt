package com.sigismund.models

data class Ingredient(
    val userId: Int,
    val recipeId: Int = -1,
    val name: String,
    val quantity: String,
    var isInTheList: Boolean = false
)