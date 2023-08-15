package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenClaim(
    val name: String,
    val value: String
)
