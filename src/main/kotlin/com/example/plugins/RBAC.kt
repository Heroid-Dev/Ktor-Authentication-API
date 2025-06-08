package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

class PluginConfiguration {
    var roleSet: Set<String> = emptySet()
}

val roleBaseAccessConfigAuthorization = createRouteScopedPlugin(
    name = "RBAC Authorization",
    createConfiguration = ::PluginConfiguration
) {
    val roles = pluginConfig.roleSet

    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val roleExtract =
                call
                    .principal<JWTPrincipal>()
                    ?.payload
                    ?.getClaim("roleUser")
                    ?.asString()
            if (!roles.contains(roleExtract)) {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}