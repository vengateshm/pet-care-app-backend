package dev.vengateshm.routes

import dev.vengateshm.db.tables.HospitalTable
import dev.vengateshm.models.Hospital
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.hospitalRoutes() {
    route("/api/v1/hospitals") {
        get {
            val hospitals = transaction {
                HospitalTable.selectAll().map {
                    Hospital(
                        it[HospitalTable.id].value,
                        it[HospitalTable.name],
                        it[HospitalTable.location],
                        it[HospitalTable.latitude],
                        it[HospitalTable.longitude],
                        it[HospitalTable.email],
                        it[HospitalTable.phone],
                        it[HospitalTable.website]
                    )
                }
            }
            call.respond(hospitals)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val hospital = transaction {
                    HospitalTable.select { HospitalTable.id eq id }.singleOrNull()
                }

                if (hospital != null) {
                    call.respond(
                        Hospital(
                            hospital[HospitalTable.id].value,
                            hospital[HospitalTable.name],
                            hospital[HospitalTable.location],
                            hospital[HospitalTable.latitude],
                            hospital[HospitalTable.longitude],
                            hospital[HospitalTable.email],
                            hospital[HospitalTable.phone],
                            hospital[HospitalTable.website]
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
            val newHospital = call.receive<Hospital>()
            val insertedHospitalId = transaction {
                HospitalTable.insertAndGetId {
                    it[name] = newHospital.name
                    it[location] = newHospital.location
                    it[latitude] = newHospital.latitude
                    it[longitude] = newHospital.longitude
                    it[email] = newHospital.email
                    it[phone] = newHospital.phone
                    it[website] = newHospital.website
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to insertedHospitalId.value))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updatedHospital = call.receive<Hospital>()

            if (id != null) {
                val updatedRowCount = transaction {
                    HospitalTable.update({ HospitalTable.id eq id }) {
                        it[name] = updatedHospital.name
                        it[location] = updatedHospital.location
                        it[latitude] = updatedHospital.latitude
                        it[longitude] = updatedHospital.longitude
                        it[email] = updatedHospital.email
                        it[phone] = updatedHospital.phone
                        it[website] = updatedHospital.website
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
                    HospitalTable.deleteWhere { HospitalTable.id eq id }
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
            val newHospitals = call.receive<List<Hospital>>()
            val insertedHospitalIds = transaction {
                newHospitals.map { hospital ->
                    HospitalTable.insertAndGetId {
                        it[name] = hospital.name
                        it[location] = hospital.location
                        it[latitude] = hospital.latitude
                        it[longitude] = hospital.longitude
                        it[email] = hospital.email
                        it[phone] = hospital.phone
                        it[website] = hospital.website
                    }
                }
            }
            call.respond(HttpStatusCode.Created, insertedHospitalIds.map { it.value })
        }
    }
}