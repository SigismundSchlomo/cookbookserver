package com.sigismund.data.cookingstep

import com.sigismund.data.recipe.Recipes
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object CookingSteps : IntIdTable() {
    val recipeId: Column<Int> = integer("recipeId").references(Recipes.id)
    val description = varchar("description", 512)
}