package com.example.domain.repository

import com.example.domain.models.local.TodoTask
import com.example.domain.models.local.User
import org.bson.types.ObjectId

interface SignUpRepository {
    suspend fun getAllUsers():List<User>
    suspend fun insertUser(user:User):Boolean
    suspend fun updateUser(user:User):Boolean
    suspend fun deleteUser(userId:String):Boolean
    suspend fun deleteAllUsers():Boolean
    suspend fun getUserById(userId:String):User?
    suspend fun getUserByUsername(username:String):User?

    suspend fun getAllTasksUser():List<TodoTask>


}