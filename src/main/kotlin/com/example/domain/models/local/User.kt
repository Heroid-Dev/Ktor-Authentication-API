package com.example.domain.models.local

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val userId:ObjectId= ObjectId(),
    val username:String,
    var password:String,
    var salt:String,
    val role:String,
    var list:MutableList<TodoTask> = mutableListOf()
)