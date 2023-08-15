package dev.vengateshm.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.vengateshm.models.TokenClaim
import dev.vengateshm.models.TokenConfig
import java.util.*

class JWTTokenService : TokenService {
    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(config.jwtAudience)
            .withIssuer(config.jwtIssuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
        claims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(config.jwtSecret))
    }
}