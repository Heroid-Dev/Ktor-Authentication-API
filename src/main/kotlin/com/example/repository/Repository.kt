package com.example.repository

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.domain.models.local.TokenStorage
import com.example.domain.models.local.User
import com.example.domain.models.request.SignInRequest
import com.example.domain.models.response.SignInResponse
import com.example.domain.repository.SignUpRepository
import com.example.domain.repository.TokenRepository
import com.example.security.hashing.HashingServices
import com.example.security.hashing.SaltedHash
import com.example.security.token.JwtServices
import org.bson.types.ObjectId

class Repository(
    private val signUpRepository: SignUpRepository,
    private val hashingServices: HashingServices,
    private val jwtServices: JwtServices,
    private val tokenRepository: TokenRepository
) {

    fun createSaltedHash(value: String, saltLength: Int = 8): SaltedHash =
        hashingServices.createSaltedHash(value, saltLength)

    fun verifySaltedHash(value: String, saltedHash: SaltedHash): Boolean =
        hashingServices.verifySaltedHash(value, saltedHash)

    suspend fun getAllUsers(): List<User> = signUpRepository.getAllUsers()
    suspend fun insertUser(user: User): Boolean = signUpRepository.insertUser(user)
    suspend fun updateUser(user: User): Boolean = signUpRepository.updateUser(user)
    suspend fun deleteUser(userId: String): Boolean {
        tokenRepository.deleteTokenStorageById(ObjectId(userId))
        return signUpRepository.deleteUser(userId)
    }

    suspend fun deleteAllUsers(): Boolean {
        tokenRepository.deleteAllTokenStorage()
        return signUpRepository.deleteAllUsers()
    }

    suspend fun getUserById(userId: String): User? = signUpRepository.getUserById(userId)
    suspend fun getUserByUsername(username: String): User? = signUpRepository.getUserByUsername(username)


    suspend fun signInUser(signInRequest: SignInRequest): SignInResponse? {
        val foundUser = signUpRepository.getUserByUsername(signInRequest.username) ?: return null
        return hashingServices.verifySaltedHash(
            value = signInRequest.password,
            saltedHash = SaltedHash(hash = foundUser.password, salt = foundUser.salt)
        ).let {
            if (it) {
                val accessToken = jwtServices.createAccessToken(signInRequest.username, foundUser.role)
                val refreshToken = jwtServices.createRefreshToken(signInRequest.username, foundUser.role)
                tokenRepository.saveTokenStorage(
                    TokenStorage(
                        idToken = foundUser.userId,
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
                SignInResponse(
                    accessToken = accessToken
                )
            } else null
        }
    }

    suspend fun refresh(refreshToken: String): SignInResponse? {
        val tokenDecoded = refreshTokenVerify(refreshToken) ?: return null
        val idToken = tokenRepository.getIdByRefreshToken(refreshToken) ?: return null
        val foundUser = getUserById(idToken.toString()) ?: return null
        return if (foundUser.username == tokenDecoded.getClaim("usernameUser").asString()) {
            val newAccessToken = jwtServices.createAccessToken(foundUser.username, foundUser.role)
            tokenRepository.updateTokenStorage(
                TokenStorage(
                    foundUser.userId,
                    refreshToken = refreshToken,
                    accessToken = newAccessToken
                )
            )
            SignInResponse(
                accessToken = newAccessToken
            )
        } else null
    }

    private fun refreshTokenVerify(refreshToken: String): DecodedJWT? {
        val decodedJwt = try {
            jwtServices.jwtVerifier.verify(refreshToken)
        } catch (e: Exception) {
            null
        }
        return decodedJwt?.let {
            if (jwtServices.checkExistAudience(it.audience.first())) {
                decodedJwt
            } else null
        }
    }

}