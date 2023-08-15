package dev.vengateshm.routes

import dev.vengateshm.models.request.*
import dev.vengateshm.models.response.TokenResponse
import dev.vengateshm.service.AuthService
import dev.vengateshm.service.SocialAuthService
import dev.vengateshm.utils.JWT_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.authRoutes(
    authService: AuthService,
    socialAuthService: SocialAuthService
) {
    routing {
        route("/api/v1/auth") {
            post("/signup") {
                val signUpRequest = call.receiveNullable<SignUpRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (!signUpRequest.areFieldsValid()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (!signUpRequest.isPasswordValid()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val user = authService.signup(signUpRequest)
                call.respond(HttpStatusCode.OK, user)
            }

            post("/login") {
                val loginRequest = call.receiveNullable<LoginRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (!loginRequest.areFieldsValid()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val (token, user) = authService.login(loginRequest)
                call.respond(HttpStatusCode.OK, TokenResponse(token = token, user = user))
            }

            post("/signup-google") {
                val request = call.receiveNullable<GoogleSignupRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (request.idToken.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val user = socialAuthService.signup(idToken = request.idToken)
                call.respond(HttpStatusCode.OK, user)
            }

            post("/signin-google") {
                val request = call.receiveNullable<GoogleSignupRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (request.idToken.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val (token, user) = socialAuthService.login(idToken = request.idToken)
                call.respond(HttpStatusCode.OK, TokenResponse(token = token, user = user))
            }

            authenticate(JWT_AUTH) {
                get {
                    call.respond(HttpStatusCode.OK)
                }

                get("/secret") {
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal?.getClaim("email", String::class)
                    call.respond(HttpStatusCode.OK, "Your email is $email")
                }
            }
        }
    }
}