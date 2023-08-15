package dev.vengateshm.models.response

import dev.vengateshm.models.User
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String,
    val user: User? = null
)
