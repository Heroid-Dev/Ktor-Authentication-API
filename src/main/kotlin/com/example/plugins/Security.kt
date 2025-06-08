package com.example.plugins

import com.example.security.token.JwtServices
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {

    val jwtServices by inject<JwtServices>()

    authentication {
        jwt {
            realm = jwtServices.realm
            verifier(jwtServices.jwtVerifier)
            validate { credential ->
                jwtServices.customValidator(credential)
            }
        }
    }
}
