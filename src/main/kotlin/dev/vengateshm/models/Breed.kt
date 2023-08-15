package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class Breed(
    val id: Int? = null,
    val name: String,
    val speciesTypeId: Int?
)
