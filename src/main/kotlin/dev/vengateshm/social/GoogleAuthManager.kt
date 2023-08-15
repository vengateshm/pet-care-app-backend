package dev.vengateshm.social

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

object GoogleAuthManager {
    fun verifyGoogleIdToken(idToken: String, clientId: String): VerifiedPayload? {
        val verifier =
            GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory()).setAudience(listOf(clientId)).build()
        val googleIdToken = verifier.verify(idToken) ?: return null
        if (googleIdToken.payload == null) return null
        // Get profile information from payload
        // Get profile information from payload
        val email: String = googleIdToken.payload.email
        val emailVerified: Boolean = java.lang.Boolean.valueOf(googleIdToken.payload.emailVerified)
        val name = googleIdToken.payload["name"] as? String
        return VerifiedPayload(email = email, isVerified = emailVerified, name = name ?: "")
    }
}

data class VerifiedPayload(val email: String, val isVerified: Boolean, val name: String)