package com.sigismund.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object RecipesTable : IntIdTable() {
    val header = varchar("header", 70)
    val body = varchar("body", 512)
}

class RecipeDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RecipeDao>(RecipesTable)
    var header by RecipesTable.header
    var body by RecipesTable.body
}