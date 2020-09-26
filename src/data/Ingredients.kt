package com.sigismund.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Ingredients : IntIdTable() {
    val recipeId: Column<Int> = integer("userId").references(Recipes.id)
    val name = varchar("name", 64)
    val quantity = varchar("quantity", 32)
}