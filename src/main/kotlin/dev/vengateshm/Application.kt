package dev.vengateshm

import dev.vengateshm.db.AppDatabaseConfig
import dev.vengateshm.db.DatabaseFactory
import dev.vengateshm.models.TokenConfig
import dev.vengateshm.models.response.ErrorResponse
import dev.vengateshm.plugins.installJWT
import dev.vengateshm.repository.UserRepositoryImpl
import dev.vengateshm.routes.*
import dev.vengateshm.security.hashing.SHA256HashingService
import dev.vengateshm.security.token.JWTTokenService
import dev.vengateshm.service.AuthServiceImpl
import dev.vengateshm.service.GoogleAuthServiceImpl
import dev.vengateshm.service.InvalidPasswordException
import dev.vengateshm.service.UserAlreadyExistsException
import dev.vengateshm.utils.authenticatedRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun main() {
    embeddedServer(Netty, port = 8888, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    val dbJdbcUrl = System.getenv("db.jdbcurl")
    val dbUser = System.getenv("db.user")
    val dbPassword = System.getenv("db.password")
    val appDatabaseConfig = AppDatabaseConfig(
        driverClassName = "com.mysql.cj.jdbc.Driver",
        jdbcUrl = dbJdbcUrl,
        username = dbUser,
        password = dbPassword
    )
    DatabaseFactory.init(appDatabaseConfig)

    val tokenConfig = TokenConfig(
//        jwtIssuer = environment.config.property("jwt.issuer").getString(),
        // OS level export Linux, SET Windows
        jwtSecret = System.getenv("jwt.secret"),
        jwtIssuer = System.getenv("jwt.issuer"),
        jwtAudience = System.getenv("jwt.audience"),
        jwtRealm = System.getenv("jwt.realm"),
        expiresIn = 365L * 24L * 60L * 60L * 1000L,
    )

    val clientId = System.getenv("google_client_id")
        ?: throw RuntimeException("Client id not available for social login/signup")

    val authService = AuthServiceImpl(
        userRepository = UserRepositoryImpl(),
        hashService = SHA256HashingService(),
        tokenService = JWTTokenService(),
        tokenConfig = tokenConfig
    )
    val socialAuthService = GoogleAuthServiceImpl(
        userRepository = UserRepositoryImpl(),
        tokenService = JWTTokenService(),
        tokenConfig = tokenConfig,
        clientId = clientId
    )

    install(ContentNegotiation) {
        /*gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }*/
        json()
    }
    install(StatusPages) {
        exception<BadRequestException> { call, e ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message!!))
        }
        exception<UserAlreadyExistsException> { call, e ->
            call.respond(HttpStatusCode.Forbidden, ErrorResponse(e.message!!))
        }
        exception<InvalidPasswordException> { call, e ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(e.message!!))
        }
        exception<Throwable> { call, e ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.localizedMessage))
        }
    }
    install(CallLogging)

    installJWT(tokenConfig)

    welcomeRoute()
    authRoutes(authService, socialAuthService)

    authenticatedRoutes {
        breedRoutes()
        speciesTypeRoutes()
        specializationRoutes()
        hospitalRoutes()
        physicianRoutes()
        petRoutes()
        appServiceRoutes()
        appointmentRoutes()
        timeSlotRoutes()
    }
}

fun Application.welcomeRoute() {
    routing {
        get {
            call.respond("Welcome to Pet Care App!")
        }
    }
}