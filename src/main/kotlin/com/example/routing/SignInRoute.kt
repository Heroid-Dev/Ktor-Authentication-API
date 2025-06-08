package com.example.routing

import com.example.domain.models.local.TodoTask
import com.example.domain.models.local.User
import com.example.domain.models.request.RefreshTokenRequest
import com.example.domain.models.request.SignInRequest
import com.example.repository.Repository
import com.example.security.hashing.SaltedHash
import com.example.utility.authorize
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.signInRoute() {
    val repository: Repository by inject()
    route("sign-in") {
        post {
            val request = call.receiveNullable<SignInRequest>() ?: return@post call.respond(
                HttpStatusCode.BadRequest
            )
            val signInResponse = repository.signInUser(signInRequest = request) ?: return@post call.respond(
                HttpStatusCode.Unauthorized
            )
            return@post call.respond(signInResponse)
        }
        post("refresh") {
            val refreshTokenRequest =
                call.receiveNullable<RefreshTokenRequest>() ?: return@post call.respond(HttpStatusCode.BadRequest)
            val signInResponse =
                repository.refresh(refreshToken = refreshTokenRequest.refreshToken) ?: return@post call.respond(
                    HttpStatusCode.Unauthorized
                )
            return@post call.respond(signInResponse)
        }
        authenticate {
            put {
                val request = call.receiveNullable<SignInRequest>() ?: return@put call.respond(
                    HttpStatusCode.BadRequest
                )
                val foundUser =
                    repository.getUserByUsername(request.username) ?: return@put call.respond(HttpStatusCode.NotFound)
                if (foundUser.username != call.principal<JWTPrincipal>()?.payload?.getClaim("usernameUser")
                        ?.asString()
                ) {
                    return@put call.respond(HttpStatusCode.Unauthorized)
                }
                val saltedHash = repository.createSaltedHash(request.password)
                foundUser.password = saltedHash.hash
                foundUser.salt = saltedHash.salt
                repository.updateUser(
                    foundUser
                ).let {
                    if (it)
                        return@put call.respond(HttpStatusCode.OK)
                    else
                        return@put call.respond(HttpStatusCode.Conflict)
                }
            }
        }
        authenticate {
            authorize("USER") {
                post {
                    val todoTask =
                        call.receiveNullable<TodoTask>() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val usernameUser =
                        call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString()
                            ?: return@post call.respond(
                                HttpStatusCode.Unauthorized
                            )
                    val foundUser =
                        repository.getUserByUsername(usernameUser) ?: return@post call.respond(
                            HttpStatusCode.NotFound
                        )
                    foundUser.list.add(todoTask)
                    repository.updateUser(foundUser)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }


    }
}