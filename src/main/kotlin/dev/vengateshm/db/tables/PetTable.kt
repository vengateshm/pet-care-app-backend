package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object PetTable : IntIdTable("pet") {
    val name = varchar("name", 255)
    val gender = varchar("gender", 10)
    val dob = varchar("dob", 20)
    val imageUrl = varchar("image_url", 255)
    val userId = integer("user_id").references(UserTable.id).nullable()
    val speciesTypeId = integer("species_type_id").references(SpeciesTypeTable.id).nullable()
    val breedId = integer("breed_id").references(BreedTable.id).nullable()
}