package com.example.utility

import com.example.plugins.roleBaseAccessConfigAuthorization
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.authorize(
    vararg roleS:String,
    build:Route.()->Unit
){
    install(roleBaseAccessConfigAuthorization){
        roleSet = roleS.toSet()
    }
    build()
}