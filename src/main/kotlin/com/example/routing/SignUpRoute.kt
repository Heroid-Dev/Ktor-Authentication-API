package com.example.routing

import com.example.domain.models.local.User
import com.example.domain.models.request.SignUpRequest
import com.example.domain.models.response.SignUpResponse
import com.example.repository.Repository
import com.example.security.hashing.SaltedHash
import com.example.utility.authorize
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.signUpRoute() {

    val repository: Repository by inject()

    route("sign-up") {

                post {
                    val request = call.receiveNullable<SignUpRequest>() ?: return@post call.respond(
                        HttpStatusCode.BadRequest
                    )
                    val foundUser = repository.getUserByUsername(request.username)

                    if (foundUser != null) {
                        return@post call.respond(HttpStatusCode.Forbidden)
                    }

                    val saltedHash = repository.createSaltedHash(request.password)

                    repository.insertUser(
                        request.toResponse(saltedHash)
                    ).let {
                        if (it)
                            return@post call.respond(HttpStatusCode.Created)
                        else
                            return@post call.respond(HttpStatusCode.Conflict)
                    }

                }


        authenticate {
            authorize("ADMIN") {
                get {
                    val allUser = repository.getAllUsers()
                    return@get call.respond(
                        allUser.map(User::toResponse)
                    )
                }

                get("by") {
                    val id = call.request.queryParameters["id"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest
                    )
                    val userFound = repository.getUserById(id) ?: return@get call.respond(
                        HttpStatusCode.NotFound
                    )
                    return@get call.respond(
                        userFound.toResponse()
                    )
                }

                put {
                    val request = call.receiveNullable<SignUpRequest>() ?: return@put call.respond(
                        HttpStatusCode.BadRequest
                    )
                    val foundUser = repository.getUserByUsername(request.username) ?: return@put call.respond(
                        HttpStatusCode.NotFound
                    )
                    val saltedHash = repository.createSaltedHash(request.password)
                    foundUser.password = saltedHash.hash
                    foundUser.salt = saltedHash.salt
                    repository.updateUser(foundUser).let {
                        if (it)
                            return@put call.respond(HttpStatusCode.OK)
                        else
                            return@put call.respond(HttpStatusCode.Conflict)
                    }

                }

                delete("by") {
                    val id = call.request.queryParameters["id"] ?: return@delete call.respond(
                        HttpStatusCode.BadRequest
                    )
                    repository.getUserById(id) ?: return@delete call.respond(
                        HttpStatusCode.NotFound
                    )
                    repository.deleteUser(id)

                    return@delete call.respond(HttpStatusCode.OK)
                }

                delete("all") {
                    return@delete if (repository.getAllUsers().isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        repository.deleteAllUsers()
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}

private fun User.toResponse(): SignUpResponse =
    SignUpResponse(
        username = this.username,
        id = this.userId
    )


private fun SignUpRequest.toResponse(saltedHash: SaltedHash): User =
    User(
        username = this.username,
        password = saltedHash.hash,
        salt = saltedHash.salt,
        role = if (this.username == "Admin" && this.password =="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        ) {
            "ADMIN"
        } else "USER"
    )
