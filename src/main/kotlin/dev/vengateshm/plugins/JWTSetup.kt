package dev.vengateshm.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.vengateshm.models.TokenConfig
import dev.vengateshm.utils.JWT_AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.installJWT(tokenConfig: TokenConfig) {
    authentication {
        jwt(JWT_AUTH) {
            realm = tokenConfig.jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256(tokenConfig.jwtSecret)).withAudience(tokenConfig.jwtAudience)
                    .withIssuer(tokenConfig.jwtIssuer).build()
            )
            validate { credential ->
                /*if (jwtCredential.payload.getClaim("email").asString() != null) {
                    JWTPrincipal(jwtCredential.payload)
                } else null*/
                if (credential.payload.audience.contains(tokenConfig.jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}