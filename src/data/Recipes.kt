package com.sigismund.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column


object Recipes : IntIdTable() {
    val userId: Column<Int> = integer("userId").references(Users.id)
    val header = varchar("header", 70)
    val body = varchar("body", 512)
}