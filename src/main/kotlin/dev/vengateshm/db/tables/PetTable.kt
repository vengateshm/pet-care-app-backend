package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object PetTable : IntIdTable("pet") {
    val name = varchar("name", 255)
    val gender = varchar("gender", 10)
    val dob = varchar("dob", 20)
    val imageUrl = varchar("image_url", 255)
    val userId = integer("user_id")
    val speciesTypeId = integer("species_type_id")
    val breedId = integer("breed_id")
}