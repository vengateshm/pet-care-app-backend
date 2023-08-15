package dev.vengateshm.routes

import dev.vengateshm.db.tables.SpeciesTypeTable
import dev.vengateshm.models.SpeciesType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.speciesTypeRoutes() {
    route("/api/v1/species-types") {
        get {
            val speciesTypes = transaction {
                SpeciesTypeTable.selectAll().map {
                    SpeciesType(it[SpeciesTypeTable.id].value, it[SpeciesTypeTable.name])
                }
            }
            call.respond(speciesTypes)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val speciesType = transaction {
                    SpeciesTypeTable.select { SpeciesTypeTable.id eq id }.singleOrNull()
                }

                if (speciesType != null) {
                    call.respond(
                        SpeciesType(
                            speciesType[SpeciesTypeTable.id].value, speciesType[SpeciesTypeTable.name]
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post {
            val newSpeciesType = call.receive<SpeciesType>()
            val insertedSpeciesTypeId = transaction {
                SpeciesTypeTable.insertAndGetId {
                    it[name] = newSpeciesType.name
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to insertedSpeciesTypeId.value))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updatedSpeciesType = call.receive<SpeciesType>()

            if (id != null) {
                val updatedRowCount = transaction {
                    SpeciesTypeTable.update({ SpeciesTypeTable.id eq id }) {
                        it[name] = updatedSpeciesType.name
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
                    SpeciesTypeTable.deleteWhere { SpeciesTypeTable.id eq id }
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
            val newSpeciesTypes = call.receive<List<SpeciesType>>()
            val insertedSpeciesTypeIds = transaction {
                newSpeciesTypes.map { speciesType ->
                    SpeciesTypeTable.insertAndGetId {
                        it[name] = speciesType.name
                    }
                }
            }
            call.respond(HttpStatusCode.Created, insertedSpeciesTypeIds.map { it.value })
        }
    }
}