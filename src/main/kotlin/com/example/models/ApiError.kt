package com.example.models

import io.ktor.http.*

data class ApiError(
    val code: HttpStatusCode,
    override val message: String
) : Throwable(message)
