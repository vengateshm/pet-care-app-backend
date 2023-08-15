package dev.vengateshm.routes

import dev.vengateshm.db.tables.SpecializationTable
import dev.vengateshm.models.Specialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.specializationRoutes() {
    route("/api/v1/specializations") {
        get {
            val specializations = transaction {
                SpecializationTable.selectAll().map {
                    Specialization(
                        it[SpecializationTable.id].value,
                        it[SpecializationTable.name],
                        it[SpecializationTable.description]
                    )
                }
            }
            call.respond(specializations)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val specialization = transaction {
                    SpecializationTable.select { SpecializationTable.id eq id }.singleOrNull()
                }

                if (specialization != null) {
                    call.respond(
                        Specialization(
                            specialization[SpecializationTable.id].value,
                            specialization[SpecializationTable.name],
                            specialization[SpecializationTable.description]
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
            val newSpecialization = call.receive<Specialization>()
            val insertedSpecializationId = transaction {
                SpecializationTable.insertAndGetId {
                    it[name] = newSpecialization.name
                    it[description] = newSpecialization.description
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to insertedSpecializationId.value))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updatedSpecialization = call.receive<Specialization>()

            if (id != null) {
                val updatedRowCount = transaction {
                    SpecializationTable.update({ SpecializationTable.id eq id }) {
                        it[name] = updatedSpecialization.name
                        it[description] = updatedSpecialization.description
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
                    SpecializationTable.deleteWhere { SpecializationTable.id eq id }
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
            val newSpecializations = call.receive<List<Specialization>>()
            val insertedSpecializationIds = transaction {
                newSpecializations.map { specialization ->
                    SpecializationTable.insertAndGetId {
                        it[name] = specialization.name
                        it[description] = specialization.description
                    }
                }
            }
            call.respond(HttpStatusCode.Created, insertedSpecializationIds.map { it.value })
        }
    }
}