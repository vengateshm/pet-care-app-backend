package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AppointmentTable : IntIdTable("appointment") {
    val physicianId = integer("physician_id")
    val timeSlotId = integer("time_slot_id")
    val userId = integer("user_id")
    val appointmentCreatedAt = datetime("appointment_created_at").defaultExpression(CurrentDateTime)
}