package com.sigismund.data

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
}