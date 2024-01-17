package com.example.utils

import com.example.models.ApiError
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.http.*

fun defaultErrorHandler(e: Throwable, logger: KLogger): Nothing {
    logger.debug { e.localizedMessage }

    if (e is ApiError) {
        throw e
    }
    throw ApiError(HttpStatusCode.InternalServerError, "Internal server error")
}