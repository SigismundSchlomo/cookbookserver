package com.sigismund.data.recipe

import com.sigismund.data.user.Users
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column


object Recipes : IntIdTable() {
    val userId: Column<Int> = integer("userId").references(Users.id)
    val header = varchar("header", 70)
    val body = varchar("body", 512)
}