package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class Specialization(
    val id: Int? = null,
    val name: String,
    val description: String
)
