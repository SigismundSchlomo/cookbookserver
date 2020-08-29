package com.sigismund.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object Recipes : IntIdTable() {
    val header = varchar("header", 70)
    val body = varchar("body", 512)
}