package com.example.routing

import io.ktor.server.routing.*

fun Route.mainRoute(){
    signUpRoute()
    signInRoute()
}