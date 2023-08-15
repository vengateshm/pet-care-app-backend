package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("user") {
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val password = text("password")
    val salt = text("salt")
}