package com.example.repository

import com.example.domain.models.local.TokenStorage
import com.example.domain.repository.TokenRepository
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

class TokenRepositoryImpl : TokenRepository {

    private val client = KMongo.createClient().coroutine
    private val database = client.getDatabase("LoginProjectPart1")
    private val tokens = database.getCollection<TokenStorage>()

    override suspend fun saveTokenStorage(tokenStorage: TokenStorage) {
        getTokenStorageById(tokenStorage.idToken).let {
            if (it == null) {
                tokens.insertOne(tokenStorage)
            } else {
                updateTokenStorage(tokenStorage)
            }
        }
    }

    override suspend fun getTokenStorageById(id: ObjectId): TokenStorage? {
        return tokens.findOneById(id)
    }

    override suspend fun getIdByRefreshToken(refreshToken: String): ObjectId? =
        tokens.findOne(TokenStorage::refreshToken eq refreshToken)?.idToken


    override suspend fun updateTokenStorage(tokenStorage: TokenStorage): Boolean =
        tokens.updateOneById(tokenStorage.idToken, tokenStorage).wasAcknowledged()


    override suspend fun deleteTokenStorageById(id: ObjectId): Boolean =
        tokens.deleteOneById(id).wasAcknowledged()

    override suspend fun deleteAllTokenStorage(): Boolean =
        tokens.deleteMany().wasAcknowledged()

}