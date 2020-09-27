package com.sigismund.domain.services.routingmodels

data class AuthRequest(
        val email: String = "",
        val name: String = "",
        val password: String = ""
)