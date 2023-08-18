package dev.vengateshm.routes

import dev.vengateshm.db.tables.HospitalTable
import dev.vengateshm.db.tables.PhysicianTable
import dev.vengateshm.db.tables.SpecializationTable
import dev.vengateshm.models.Physician
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.physicianRoutes() {
    route("/api/v1/physicians") {
        get {
            val physicians = transaction {
                PhysicianTable.leftJoin(HospitalTable, { hospitalId }, { HospitalTable.id })
                    .leftJoin(SpecializationTable, { PhysicianTable.specializationId }, { SpecializationTable.id })
                    .selectAll().map {
                        Physician(
                            it[PhysicianTable.id].value,
                            it[PhysicianTable.name],
                            it[PhysicianTable.email],
                            it[PhysicianTable.phone],
                            it[PhysicianTable.hospitalId],
                            it[PhysicianTable.specializationId],
                            it[HospitalTable.name], // Adding hospital name
                            it[SpecializationTable.name] // Adding specialization name
                        )
                    }
            }
            call.respond(physicians)
        }

        get("/specialization/{specializationId}") {
            val specializationId = call.parameters["specializationId"]?.toIntOrNull()
            if (specializationId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid specializationId")
                return@get
            }

            val physicians = transaction {
                PhysicianTable.innerJoin(HospitalTable, { hospitalId }, { HospitalTable.id })
                    .innerJoin(SpecializationTable, { PhysicianTable.specializationId }, { SpecializationTable.id })
                    .select { PhysicianTable.specializationId eq specializationId }
                    .map {
                        Physician(
                            id = it[PhysicianTable.id].value,
                            name = it[PhysicianTable.name],
                            email = it[PhysicianTable.email],
                            phone = it[PhysicianTable.phone],
                            hospitalId = it[HospitalTable.id].value,
                            specializationId = it[SpecializationTable.id].value,
                            hospitalName = it[HospitalTable.name],
                            specializationName = it[SpecializationTable.name]
                        )
                    }
            }

            call.respond(physicians)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val physician = transaction {
                    PhysicianTable.leftJoin(HospitalTable, { hospitalId }, { HospitalTable.id })
                        .leftJoin(SpecializationTable, { PhysicianTable.specializationId }, { SpecializationTable.id })
                        .select { PhysicianTable.id eq id }.singleOrNull()
                }

                if (physician != null) {
                    call.respond(
                        Physician(
                            physician[PhysicianTable.id].value,
                            physician[PhysicianTable.name],
                            physician[PhysicianTable.email],
                            physician[PhysicianTable.phone],
                            physician[PhysicianTable.hospitalId],
                            physician[PhysicianTable.specializationId],
                            physician[HospitalTable.name], // Adding hospital name
                            physician[SpecializationTable.name] // Adding specialization name
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
            val newPhysician = call.receive<Physician>()
            val insertedPhysicianId = transaction {
                PhysicianTable.insertAndGetId {
                    it[name] = newPhysician.name
                    it[email] = newPhysician.email
                    it[phone] = newPhysician.phone
                    it[hospitalId] = newPhysician.hospitalId!!
                    it[specializationId] = newPhysician.specializationId!!
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to insertedPhysicianId.value))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updatedPhysician = call.receive<Physician>()

            if (id != null) {
                val updatedRowCount = transaction {
                    PhysicianTable.update({ PhysicianTable.id eq id }) {
                        it[name] = updatedPhysician.name
                        it[email] = updatedPhysician.email
                        it[phone] = updatedPhysician.phone
                        it[hospitalId] = updatedPhysician.hospitalId!!
                        it[specializationId] = updatedPhysician.specializationId!!
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
                    PhysicianTable.deleteWhere { PhysicianTable.id eq id }
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
            val newPhysicians = call.receive<List<Physician>>()
            val insertedPhysicianIds = transaction {
                newPhysicians.map { physician ->
                    PhysicianTable.insertAndGetId {
                        it[name] = physician.name
                        it[email] = physician.email
                        it[phone] = physician.phone
                        it[hospitalId] = physician.hospitalId!!
                        it[specializationId] = physician.specializationId!!
                    }
                }
            }
            call.respond(HttpStatusCode.Created, insertedPhysicianIds.map { it.value })
        }
    }
}