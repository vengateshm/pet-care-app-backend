package dev.vengateshm.models.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)
