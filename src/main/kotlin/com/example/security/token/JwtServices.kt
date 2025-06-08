package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.repository.SignUpRepository
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtServices(
    private val application: Application,
    private val signUpRepository: SignUpRepository
) {

    private val audience = getConfigProperty("jwt.audience")
    private val issuer = getConfigProperty("jwt.issuer")
    private val secret = getConfigProperty("jwt.secret")
    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withIssuer(issuer)
            .withAudience(audience)
            .build()

    fun createAccessToken(username: String, role: String): String =
        createJwtToken(username, role, 3_600_000)

    fun createRefreshToken(username: String, role: String): String =
        createJwtToken(username, role, 86_400_000)

    private fun createJwtToken(username: String, role: String, expireIn: Long): String =
        JWT
            .create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("usernameUser", username)
            .withClaim("roleUser", role)
            .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
            .sign(Algorithm.HMAC256(secret))

    fun checkExistAudience(value: String): Boolean =
        this.audience == value

    private fun audienceChecked(jwtCredential: JWTCredential): Boolean =
        jwtCredential.payload.audience.contains(this.audience)


    suspend fun customValidator(jwtCredential: JWTCredential): JWTPrincipal? {
        val usernameToken = getUsernameToken(jwtCredential) ?: return null
        val userToken = signUpRepository.getUserByUsername(usernameToken)
        return if (userToken != null) {
            if (audienceChecked(jwtCredential))
                JWTPrincipal(jwtCredential.payload)
            else
                null
        } else null

    }

    private fun getUsernameToken(jwtCredential: JWTCredential): String? =
        jwtCredential.payload.getClaim("usernameUser").asString()

    private fun getConfigProperty(path: String): String =
        application.environment.config.property(path).getString()

}