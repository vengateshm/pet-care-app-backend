package dev.vengateshm.models.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String?,
    val password: String?
)

fun LoginRequest.areFieldsValid() =
    !email.isNullOrBlank() && !password.isNullOrBlank()
