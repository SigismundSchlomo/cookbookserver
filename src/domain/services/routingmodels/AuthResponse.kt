package com.sigismund.domain.services.routingmodels


data class AuthResponse(
        val token: String,
        val userId: Int,
        val userName: String,
        val email: String
)