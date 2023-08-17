package dev.vengateshm.routes

import dev.vengateshm.db.tables.TimeSlotTable
import dev.vengateshm.models.TimeSlot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.timeSlotRoutes() {
    route("/api/v1/time-slots") {
        post {
            val timeSlot = call.receive<TimeSlot>()
            val newTimeSlotId = transaction {
                TimeSlotTable.insertAndGetId {
                    it[physicianId] = timeSlot.physicianId
                    it[startTime] = timeSlot.startTime
                    it[isAvailable] = timeSlot.isAvailable
                    it[dayOfWeek] = timeSlot.dayOfWeek
                    it[month] = timeSlot.month
                    it[year] = timeSlot.year
                }
            }

            call.respond(HttpStatusCode.Created, newTimeSlotId.value)
        }

        post("/multiple") {
            val timeSlots = call.receive<List<TimeSlot>>()
            val insertedIds = transaction {
                timeSlots.map { timeSlot ->
                    TimeSlotTable.insertAndGetId {
                        it[physicianId] = timeSlot.physicianId
                        it[startTime] = timeSlot.startTime
                        it[isAvailable] = timeSlot.isAvailable
                        it[dayOfWeek] = timeSlot.dayOfWeek
                        it[month] = timeSlot.month
                        it[year] = timeSlot.year
                    }.value
                }
            }
            call.respond(HttpStatusCode.Created, insertedIds)
        }

        get("/physician/{physicianId}") {
            val physicianId = call.parameters["physicianId"]?.toIntOrNull()

            if (physicianId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid physician ID")
                return@get
            }

            val timeSlots = transaction() {
                TimeSlotTable.select { TimeSlotTable.physicianId eq physicianId }.map {
                    TimeSlot(
                        it[TimeSlotTable.id].value,
                        it[TimeSlotTable.physicianId],
                        it[TimeSlotTable.startTime],
                        it[TimeSlotTable.isAvailable],
                        it[TimeSlotTable.dayOfWeek],
                        it[TimeSlotTable.month],
                        it[TimeSlotTable.year]
                    )
                }
            }

            call.respond(timeSlots)
        }
    }
}