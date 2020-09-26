package com.sigismund.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Ingredients : IntIdTable() {
    val recipeId: Column<Int> = integer("recipeId").references(Recipes.id)
    val userId: Column<Int> = integer("userId").references(Users.id)
    val name = varchar("name", 64)
    val quantity = varchar("quantity", 32)
    val isInTheList = bool("isInTheList")
}