package dev.vengateshm.routes

import dev.vengateshm.db.tables.AppointmentTable
import dev.vengateshm.db.tables.PhysicianTable
import dev.vengateshm.db.tables.TimeSlotTable
import dev.vengateshm.models.Appointment
import dev.vengateshm.models.TimeSlot
import dev.vengateshm.models.response.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.format.DateTimeFormatter

fun Route.appointmentRoutes() {
    route("/api/v1/appointments") {
        post {
            val newAppointment = call.receive<Appointment>()

            val foundTimeSlot = transaction {
                TimeSlotTable.select { TimeSlotTable.id eq newAppointment.timeSlotId }.singleOrNull()?.let {
                    TimeSlot(
                        it[TimeSlotTable.id].value,
                        it[TimeSlotTable.physicianId].value,
                        it[TimeSlotTable.startTime],
                        it[TimeSlotTable.isAvailable],
                        it[TimeSlotTable.dayOfWeek],
                        it[TimeSlotTable.month],
                        it[TimeSlotTable.year]
                    )
                }
            }

            if (foundTimeSlot == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(error = "Time slot not found"))
                return@post
            }
            if (!foundTimeSlot.isAvailable) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    ErrorResponse(error = "Cannot create appointment as this time slot is not available")
                )
                return@post
            }

            val insertedAppointmentId = transaction {
                AppointmentTable.insertAndGetId {
                    it[physicianId] = newAppointment.physicianId
                    it[timeSlotId] = foundTimeSlot.id!!
                    it[userId] = newAppointment.userId
                }
            }
            val timeSlotRowUpdated = transaction {
                TimeSlotTable.update({ TimeSlotTable.id eq foundTimeSlot.id }) {
                    it[isAvailable] = false
                }
            }
            if (timeSlotRowUpdated < 1) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    ErrorResponse(error = "Cannot create appointment as this time slot is not available")
                )
                return@post
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to insertedAppointmentId.value))
        }

        get("/user/{id}") {
            val userId = call.parameters["id"]?.toIntOrNull()

            if (userId != null) {
                val appointments = transaction {
                    (AppointmentTable innerJoin PhysicianTable)
                        .select { AppointmentTable.userId eq userId }
                        .map {
                            val appointment = Appointment(
                                it[AppointmentTable.id].value,
                                it[AppointmentTable.physicianId].value,
                                it[AppointmentTable.timeSlotId].value,
                                it[AppointmentTable.userId].value,
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(it[AppointmentTable.appointmentCreatedAt].toJavaLocalDateTime()),
                                "",
                                ""
                            )
                            val physicianName = it[PhysicianTable.name]
                            val timeSlot = TimeSlotTable
                                .select { TimeSlotTable.id eq appointment.timeSlotId }
                                .singleOrNull()

                            if (timeSlot != null) {
                                val startTime = timeSlot[TimeSlotTable.startTime]
                                appointment.copy(physicianName = physicianName, startTime = startTime)
                            } else {
                                appointment
                            }
                        }
                }
                call.respond(appointments)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}