package com.example.domain.models.local

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class TodoTask(
    @BsonId
    val taskId:ObjectId= ObjectId(),
    val title:String,
    val description:String
)
