package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val id: Int? = null,
    val physicianId: Int,
    val timeSlotId: Int,
    val userId: Int,
    val appointmentCreatedAt: String = "",
    val physicianName: String = "",
    val startTime: String = "",
)
