package com.example.domain.models.response

import com.example.utility.ObjectIdSerializer
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class SignUpResponse(
   @Serializable(with = ObjectIdSerializer::class)
    val id:ObjectId,
    val username:String,
)