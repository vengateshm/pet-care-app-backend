package dev.vengateshm.routes

import dev.vengateshm.db.tables.BreedTable
import dev.vengateshm.models.Breed
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.breedRoutes() {
    route("/api/v1/breeds") {
        post {
            val newBreed = call.receive<Breed>()
            val insertedBreedId = transaction {
                BreedTable.insertAndGetId {
                    it[name] = newBreed.name
                    it[speciesTypeId] = newBreed.speciesTypeId!!
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to insertedBreedId))
        }

        get {
            val breeds = transaction {
                BreedTable.selectAll().map {
                    Breed(it[BreedTable.id].value, it[BreedTable.name], speciesTypeId = it[BreedTable.speciesTypeId])
                }
            }
            call.respond(breeds)
        }

        get("/speciesType/{speciesTypeId}") {
            val speciesTypeId = call.parameters["speciesTypeId"]?.toIntOrNull()
            if (speciesTypeId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val breeds = transaction {
                BreedTable.select { BreedTable.speciesTypeId eq speciesTypeId }
                    .map {
                        Breed(
                            it[BreedTable.id].value,
                            it[BreedTable.name],
                            speciesTypeId = it[BreedTable.speciesTypeId]
                        )
                    }
            }

            call.respond(breeds)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updatedBreed = call.receive<Breed>()

            if (id != null) {
                val updatedRowCount = transaction {
                    BreedTable.update({ BreedTable.id eq id }) {
                        it[name] = updatedBreed.name
                    }
                }

                if (updatedRowCount > 0) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val deletedRowCount = transaction {
                    BreedTable.deleteWhere { BreedTable.id eq id }
                }

                if (deletedRowCount > 0) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/multiple") {
            val newBreeds = call.receive<List<Breed>>()
            val insertedBreedIds = transaction {
                newBreeds.map { breed ->
                    BreedTable.insertAndGetId {
                        it[name] = breed.name
                        it[speciesTypeId] = breed.speciesTypeId!!
                    }
                }
            }
            call.respond(HttpStatusCode.Created, insertedBreedIds.map { it.value })
        }
    }
}