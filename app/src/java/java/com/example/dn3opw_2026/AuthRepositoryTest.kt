package com.example.dn3opw_2026

import org.junit.Assert.*
import org.junit.Test

class AuthRepositoryTest {

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    @Test
    fun email_is_valid() {
        assertTrue(isValidEmail("test@gmail.com"))
    }

    @Test
    fun email_is_invalid() {
        assertFalse(isValidEmail("testgmail.com"))
    }
}