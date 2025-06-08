package com.example.domain.models.local

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class TokenStorage(
    @BsonId
    val idToken: ObjectId,
    val refreshToken: String,
    val accessToken: String
)
