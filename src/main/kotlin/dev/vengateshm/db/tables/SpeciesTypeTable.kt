package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object SpeciesTypeTable : IntIdTable("species_type") {
    val name = varchar("name", 255)
}