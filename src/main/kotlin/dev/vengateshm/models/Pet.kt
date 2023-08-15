package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    val id: Int?=null,
    val name: String,
    val gender: String,
    val dob: String,
    val imageUrl: String,
    val userId: Int,
    val speciesTypeId: Int,
    val breedId: Int,
    val speciesTypeName: String?="",
    val breedName: String?=""
)
