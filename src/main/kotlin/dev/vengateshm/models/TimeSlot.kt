package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class TimeSlot(
    val id: Int?=null,
    val physicianId: Int,
    val startTime: String,
    val isAvailable: Boolean,
    val dayOfWeek: Int,
    val month: Int,
    val year: Int
)
