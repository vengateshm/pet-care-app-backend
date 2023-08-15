package dev.vengateshm.models

data class TokenConfig(
    val jwtSecret: String,
    val jwtIssuer: String,
    val jwtAudience: String,
    val jwtRealm: String,
    val expiresIn: Long,
)
