package com.example.plugins

import com.example.utils.JwtConfig
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*

fun Application.configureSecurity() {

    authentication {
        jwt {
            realm = JwtConfig.jwtRealm

            verifier(
                JwtConfig.verifyJWT()
            )

            validate { credential ->
                if (credential.payload.audience.contains(JwtConfig.jwtAudience) &&
                    credential.payload.getClaim("username").asString().isNotEmpty()
                )
                    JWTPrincipal(credential.payload)
                else null
            }

            challenge { defaultScheme, realm ->
                throw JwtConfig.TokenException.InvalidTokenException("Invalid or expired token")
            }
        }
    }
}
