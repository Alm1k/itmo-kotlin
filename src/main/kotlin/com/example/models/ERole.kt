package com.example.models

enum class ERole(val databaseId: Int) {
   DIRECTOR(1), MANAGER(2), USER(3)
}

val rolesMap = mapOf(ERole.DIRECTOR to "DIRECTOR", ERole.MANAGER to "MANAGER",
   ERole.USER to "USER")
