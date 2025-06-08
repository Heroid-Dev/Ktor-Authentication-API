package com.example.repository

import com.example.domain.models.local.TodoTask
import com.example.domain.models.local.User
import com.example.domain.repository.SignUpRepository
import com.mongodb.client.model.Projections
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.util.singleProjectionCodecRegistry
import kotlin.jvm.internal.Intrinsics.Kotlin

class SignUpRepositoryImpl : SignUpRepository {

    private val client = KMongo.createClient().coroutine
    private val database = client.getDatabase("LoginProjectPart1")
    private val userCollection = database.getCollection<User>()
    override suspend fun getAllUsers(): List<User> =
        userCollection.find().toList()

    override suspend fun insertUser(user: User): Boolean {
        val foundUser = userCollection.findOneById(user.userId)
        return if (foundUser != null) {
            updateUser(user)
        } else {
            userCollection.insertOne(user).wasAcknowledged()
        }
    }

    override suspend fun updateUser(user: User): Boolean =
        userCollection.updateOneById(user.userId, user).wasAcknowledged()


    override suspend fun deleteUser(userId: String): Boolean =
        userCollection.deleteOneById(ObjectId(userId)).wasAcknowledged()

    override suspend fun deleteAllUsers(): Boolean =
        userCollection.deleteMany(User::role eq "USER").wasAcknowledged()


    override suspend fun getUserById(userId: String): User? =
        userCollection.findOneById(ObjectId(userId))


    override suspend fun getUserByUsername(username: String): User? =
        userCollection.findOne(User::username eq username)

    override suspend fun getAllTasksUser(): List<TodoTask> =
        userCollection.find().toList().flatMap(User::list)

}
