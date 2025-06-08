package com.example.domain.models.response

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val accessToken:String
)