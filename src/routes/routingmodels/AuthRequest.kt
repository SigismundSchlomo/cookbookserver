package com.sigismund.routes.routingmodels

data class AuthRequest(
        val email: String = "",
        val name: String = "",
        val password: String = ""
)