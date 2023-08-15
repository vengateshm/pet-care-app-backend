package dev.vengateshm.models.request

import kotlinx.serialization.Serializable

@Serializable
data class GoogleSignupRequest(val idToken: String)
