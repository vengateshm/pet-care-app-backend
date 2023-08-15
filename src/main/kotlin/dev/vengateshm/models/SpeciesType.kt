package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class SpeciesType(
    val id: Int? = null,
    val name: String
)
