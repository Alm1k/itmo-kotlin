package com.example.models

enum class ERequestStatus(val databaseId: Int) {
    PENDING(1), IN_PROGRESS(2), DONE(3)
}