package dev.vengateshm.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    @Transient val password: String = "",
    @Transient val salt: String = ""
)
