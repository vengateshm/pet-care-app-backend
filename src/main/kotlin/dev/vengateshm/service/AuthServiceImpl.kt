package dev.vengateshm.service

import dev.vengateshm.models.TokenClaim
import dev.vengateshm.models.TokenConfig
import dev.vengateshm.models.User
import dev.vengateshm.models.request.LoginRequest
import dev.vengateshm.models.request.SignUpRequest
import dev.vengateshm.repository.UserRepository
import dev.vengateshm.security.hashing.HashingService
import dev.vengateshm.security.hashing.SaltedHash
import dev.vengateshm.security.token.TokenService
import io.ktor.server.plugins.*
import org.apache.commons.codec.digest.DigestUtils

class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val hashService: HashingService,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) : AuthService {
    override suspend fun login(loginRequest: LoginRequest): Pair<String, User> {
        val user = userRepository.getUserByEmail(loginRequest.email!!)
        user ?: throw NotFoundException()

        val isValidPassword = hashService.verify(
            value = loginRequest.password!!, saltedHash = SaltedHash(
                hash = user.password, salt = user.salt
            )
        )
        if (!isValidPassword) {
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${loginRequest.password}")}, Hashed PW: ${user.password}")
            throw InvalidPasswordException()
        }

        val token = tokenService.generate(
            config = tokenConfig, TokenClaim(
                name = "email", value = user.email
            )
        )
        return Pair(token, user)
    }

    override suspend fun signup(signUpRequest: SignUpRequest): User {
        val user = userRepository.getUserByEmail(signUpRequest.email!!)
        if (user != null && user.email == signUpRequest.email) throw UserAlreadyExistsException()

        val saltedHash = hashService.generateSaltedHash(signUpRequest.password!!)
        val userToAdd = User(
            name = signUpRequest.name!!, email = signUpRequest.email, password = saltedHash.hash, salt = saltedHash.salt
        )
        return userRepository.createUser(userToAdd)
    }
}

class InvalidPasswordException(message: String? = "Invalid Password") : Exception(message)
class UserAlreadyExistsException(message: String? = "User Already exists") : Exception(message)