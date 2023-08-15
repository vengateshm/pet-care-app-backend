package dev.vengateshm.routes

import dev.vengateshm.db.tables.BreedTable
import dev.vengateshm.db.tables.PetTable
import dev.vengateshm.db.tables.SpeciesTypeTable
import dev.vengateshm.models.Pet
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.petRoutes() {
    route("/api/v1/pets") {
        post {
            val pet = call.receive<Pet>()
            val id = transaction {
                PetTable.insertAndGetId {
                    it[name] = pet.name
                    it[gender] = pet.gender
                    it[dob] = pet.dob
                    it[imageUrl] = pet.imageUrl ?: ""
                    it[userId] = pet.userId
                    it[speciesTypeId] = pet.speciesTypeId
                    it[breedId] = pet.breedId
                }
            }
            call.respond(HttpStatusCode.Created, id.value)
        }

        // Multiple primary foreign key issue
        /*get("/user/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                return@get
            }
            val petsWithDetails = transaction {
                (PetTable innerJoin BreedTable innerJoin SpeciesTypeTable)
                    .select { PetTable.userId eq userId }
                    .map {
                        Pet(
                            id = it[PetTable.id].value,
                            name = it[PetTable.name],
                            gender = it[PetTable.gender],
                            dob = it[PetTable.dob],
                            imageUrl = it[PetTable.imageUrl],
                            userId = it[PetTable.userId] ?: -1,
                            speciesTypeId = it[PetTable.speciesTypeId] ?: -1,
                            breedId = it[PetTable.breedId] ?: -1,
                            breedName = it[BreedTable.name],
                            speciesTypeName = it[SpeciesTypeTable.name]
                        )
                    }
            }
            call.respond(petsWithDetails)
        }*/

        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                return@get
            }

            val petsWithDetails = transaction {
                PetTable.select { PetTable.userId eq userId }.map {
                        val breedName = BreedTable.select { BreedTable.id eq it[PetTable.breedId] }.singleOrNull()
                            ?.get(BreedTable.name) ?: "Unknown Breed"

                        val speciesTypeName =
                            SpeciesTypeTable.select { SpeciesTypeTable.id eq it[PetTable.speciesTypeId] }.singleOrNull()
                                ?.get(SpeciesTypeTable.name) ?: "Unknown Species"

                        Pet(
                            id = it[PetTable.id].value,
                            name = it[PetTable.name],
                            gender = it[PetTable.gender],
                            dob = it[PetTable.dob],
                            imageUrl = it[PetTable.imageUrl],
                            userId = it[PetTable.userId] ?: -1,
                            speciesTypeId = it[PetTable.speciesTypeId] ?: -1,
                            breedId = it[PetTable.breedId] ?: -1,
                            breedName = breedName,
                            speciesTypeName = speciesTypeName
                        )
                    }
            }

            call.respond(petsWithDetails)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val updatedPet = call.receive<Pet>()
            val updatedRowCount = transaction {
                PetTable.update({ PetTable.id eq id }) {
                    it[name] = updatedPet.name
                    it[gender] = updatedPet.gender
                    it[dob] = updatedPet.dob
                    it[imageUrl] = updatedPet.imageUrl ?: ""
                    it[userId] = updatedPet.userId
                    it[speciesTypeId] = updatedPet.speciesTypeId
                    it[breedId] = updatedPet.breedId
                }
            }
            if (updatedRowCount > 0) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Pet not found")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            val deletedRowCount = transaction {
                PetTable.deleteWhere { PetTable.id eq id }
            }
            if (deletedRowCount > 0) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Pet not found")
            }
        }

        post("/multiple") {
            val pets = call.receive<List<Pet>>()
            val insertedIds = transaction {
                pets.map { pet ->
                    PetTable.insertAndGetId {
                        it[name] = pet.name
                        it[gender] = pet.gender
                        it[dob] = pet.dob
                        it[imageUrl] = pet.imageUrl ?: ""
                        it[userId] = pet.userId
                        it[speciesTypeId] = pet.speciesTypeId
                        it[breedId] = pet.breedId
                    }.value
                }
            }
            call.respond(HttpStatusCode.Created, insertedIds)
        }
    }
}