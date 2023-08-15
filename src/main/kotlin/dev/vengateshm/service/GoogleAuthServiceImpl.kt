package dev.vengateshm.service

import dev.vengateshm.models.TokenClaim
import dev.vengateshm.models.TokenConfig
import dev.vengateshm.models.User
import dev.vengateshm.repository.UserRepository
import dev.vengateshm.security.token.TokenService
import dev.vengateshm.social.GoogleAuthManager
import io.ktor.server.plugins.*

class GoogleAuthServiceImpl(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig,
    private val clientId: String
) : SocialAuthService {
    override suspend fun login(idToken: String): Pair<String, User> {
        val verifiedPayload = GoogleAuthManager.verifyGoogleIdToken(idToken = idToken, clientId = clientId)
        verifiedPayload ?: throw BadRequestException("Invalid id token")

        val user = userRepository.getUserByEmail(verifiedPayload.email)
        user ?: throw NotFoundException()

        val token = tokenService.generate(
            config = tokenConfig, TokenClaim(
                name = "email", value = user.email
            )
        )
        return Pair(token, user)
    }

    override suspend fun signup(idToken: String): User {
        val verifiedPayload = GoogleAuthManager.verifyGoogleIdToken(idToken = idToken, clientId = clientId)
        verifiedPayload ?: throw BadRequestException("Invalid id token")

        val user = userRepository.getUserByEmail(verifiedPayload.email)
        if (user != null && user.email == verifiedPayload.email) throw UserAlreadyExistsException()

        val userToAdd = User(
            name = verifiedPayload.name, email = verifiedPayload.email, password = "", salt = ""
        )
        return userRepository.createUser(userToAdd)
    }
}