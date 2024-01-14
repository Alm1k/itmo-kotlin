package com.example.plugins

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

class PluginConfiguration {
    var roles: Set<String> = emptySet()
}
val RoleBasedAuthorizationPlugin = createRouteScopedPlugin(
    name = "RbacPlugin",
    createConfiguration = ::PluginConfiguration
) {
    val role = pluginConfig.roles
    val logger = KotlinLogging.logger {}
    logger.debug { "role : $role" }
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val tokenRole = getRoleFromToken(call)
            logger.debug { "tokenRole: $tokenRole" }
            val authorized = roles.contains(tokenRole)
            if (!authorized) {
                logger.debug { "User does not have the following role: $roles" }
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

private fun getRoleFromToken(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("role")
        ?.asString()
