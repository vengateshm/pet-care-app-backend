package dev.vengateshm.service

import dev.vengateshm.models.User
import dev.vengateshm.models.request.LoginRequest
import dev.vengateshm.models.request.SignUpRequest

interface AuthService {
    suspend fun login(loginRequest: LoginRequest): Pair<String, User>
    suspend fun signup(signUpRequest: SignUpRequest): User
}