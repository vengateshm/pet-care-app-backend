package dev.vengateshm.repository

import dev.vengateshm.models.User

interface UserRepository {
    fun createUser(user: User): User
    fun getUserByEmail(email: String): User?
}