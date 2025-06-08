package com.example.domain.repository

import com.example.domain.models.local.TokenStorage
import org.bson.types.ObjectId

interface TokenRepository {
    suspend fun saveTokenStorage(tokenStorage: TokenStorage)
    suspend fun getTokenStorageById(id: ObjectId): TokenStorage?
    suspend fun getIdByRefreshToken(refreshToken: String): ObjectId?
    suspend fun updateTokenStorage(tokenStorage: TokenStorage):Boolean
    suspend fun deleteTokenStorageById(id: ObjectId):Boolean
    suspend fun deleteAllTokenStorage():Boolean
}