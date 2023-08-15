package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object BreedTable : IntIdTable("breed") {
    val name = varchar("name", 255)
    val speciesTypeId = integer("species_type_id").references(SpeciesTypeTable.id).nullable()
}