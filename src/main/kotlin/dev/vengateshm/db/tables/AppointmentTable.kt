package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AppointmentTable : IntIdTable("appointment") {
    val physicianId = reference("physician_id", PhysicianTable)
    val timeSlotId = reference("time_slot_id", TimeSlotTable)
    val userId = reference("user_id", UserTable)
    val appointmentCreatedAt = datetime("appointment_created_at").defaultExpression(CurrentDateTime)
}