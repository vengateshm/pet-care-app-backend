package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object SpecializationTable : IntIdTable() {
    val name = varchar("name", 255)
    val description = varchar("description", 255)
}