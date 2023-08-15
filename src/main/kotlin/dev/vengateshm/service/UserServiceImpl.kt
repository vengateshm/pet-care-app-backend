package dev.vengateshm.service

import dev.vengateshm.models.User
import dev.vengateshm.repository.UserRepository
import io.ktor.server.plugins.*

class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun createUser(user: User): User {
        return userRepository.createUser(user)
    }

    override fun getUserByEmail(email: String): User {
        return userRepository.getUserByEmail(email) ?: throw BadRequestException("User not found")
    }
}