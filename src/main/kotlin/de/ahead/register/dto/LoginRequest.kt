package de.ahead.register.dto

data class LoginRequest(

    val email: String,
    val code: Int // 6-digit

)