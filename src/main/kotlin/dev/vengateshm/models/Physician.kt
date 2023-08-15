package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class Physician(
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val hospitalId: Int?,
    val specializationId: Int?,
    val hospitalName: String?,
    val specializationName: String?,
)
