package dev.vengateshm.service

import dev.vengateshm.models.User

interface SocialAuthService {
    suspend fun login(idToken: String): Pair<String, User>
    suspend fun signup(idToken: String): User
}