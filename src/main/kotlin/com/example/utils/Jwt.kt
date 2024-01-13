package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import java.util.*


data class Token (val token: String)
object JwtConfig {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())

    private val jwtSecret = appConfig.property("jwt.secret").getString()
    private val jwtIssuer = appConfig.property("jwt.issuer").getString()
    val jwtAudience = appConfig.property("jwt.audience").getString()
    val jwtRealm  =  appConfig.property("jwt.realm").getString()
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    private const val VALIDITY = 36_000_00 * 1 // 1h

    fun createJwtToken(user: User): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("login", user.login)
            .withExpiresAt(getExpiration())
            .sign(algorithm)
    }

    sealed class TokenException(message: String) : RuntimeException(message) {
        class InvalidTokenException(message: String) : TokenException(message)
    }

    fun verifyJWT(): JWTVerifier {
        return try {
            JWT.require(Algorithm.HMAC512(jwtSecret))
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .build()
        } catch (e: Exception) {
            throw TokenException.InvalidTokenException("Invalid token")
        }
    }

    private fun getExpiration() = Date(System.currentTimeMillis() + VALIDITY)
}

