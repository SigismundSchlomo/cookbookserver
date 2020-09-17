package com.sigismund.routes

data class AuthRequest(
        val email: String = "",
        val name: String = "",
        val password: String = ""
)