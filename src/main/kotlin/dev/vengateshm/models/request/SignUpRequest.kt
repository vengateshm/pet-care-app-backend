package dev.vengateshm.models.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String?,
    val email: String?,
    val password: String?
)

fun SignUpRequest.areFieldsValid() =
    !name.isNullOrBlank() && !email.isNullOrBlank() && !password.isNullOrBlank()

fun SignUpRequest.isPasswordValid() =
    !password.isNullOrBlank() && password.length >= 8
