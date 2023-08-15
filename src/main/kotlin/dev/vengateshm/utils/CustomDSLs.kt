package dev.vengateshm.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*

fun Application.authenticatedRoutes(authName: String = JWT_AUTH, configure: Route.() -> Unit) {
    routing {
        authenticate(authName) {
            configure(this)
//            this.apply(configure)
        }
    }
}