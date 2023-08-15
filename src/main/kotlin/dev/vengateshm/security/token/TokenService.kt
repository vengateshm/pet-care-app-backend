package dev.vengateshm.security.token

import dev.vengateshm.models.TokenClaim
import dev.vengateshm.models.TokenConfig

interface TokenService {
    fun generate(config: TokenConfig, vararg claims: TokenClaim): String
}