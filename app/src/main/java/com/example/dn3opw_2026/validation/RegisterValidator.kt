package com.example.dn3opw_2026.validation

object RegisterValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun validate(
        firstname: String,
        lastname: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (firstname.isBlank()) return false
        if (lastname.isBlank()) return false
        if (username.isBlank()) return false
        if (email.isBlank()) return false
        if (!emailRegex.matches(email)) return false
        if (password.length < 8) return false
        if (password != confirmPassword) return false
        return true
    }
}