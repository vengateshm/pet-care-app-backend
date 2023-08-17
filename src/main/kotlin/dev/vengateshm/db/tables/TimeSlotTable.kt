package dev.vengateshm.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object TimeSlotTable : IntIdTable("time_slot") {
    val physicianId = integer("physician_id")
    val startTime = varchar("start_time", 10)
    val isAvailable = bool("is_available")
    val dayOfWeek = integer("day_of_week")
    val month = integer("month")
    val year = integer("year")
}