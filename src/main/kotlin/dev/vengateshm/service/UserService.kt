package dev.vengateshm.service

import dev.vengateshm.models.User

interface UserService {
    fun createUser(user: User): User
    fun getUserByEmail(email: String): User
}