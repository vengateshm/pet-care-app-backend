package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class Hospital(
    val id: Int? = null,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val email: String,
    val phone: String,
    val website: String
)
