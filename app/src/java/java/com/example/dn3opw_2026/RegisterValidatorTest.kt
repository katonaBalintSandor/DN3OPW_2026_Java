package com.example.dn3opw_2026

import com.example.dn3opw_2026.validation.RegisterValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterValidatorTest {

    @Test
    fun valid_input_returns_true() {
        val result = RegisterValidator.validate(
            firstname = "Teszt",
            lastname = "Elek",
            username = "tesztuser",
            email = "teszt@example.com",
            password = "Titok123!",
            confirmPassword = "Titok123!"
        )

        assertTrue(result)
    }

    @Test
    fun invalid_email_returns_false() {
        val result = RegisterValidator.validate(
            firstname = "Teszt",
            lastname = "Elek",
            username = "tesztuser",
            email = "rosszemail",
            password = "Titok123!",
            confirmPassword = "Titok123!"
        )

        assertFalse(result)
    }

    @Test
    fun mismatched_passwords_return_false() {
        val result = RegisterValidator.validate(
            firstname = "Teszt",
            lastname = "Elek",
            username = "tesztuser",
            email = "teszt@example.com",
            password = "Titok123!",
            confirmPassword = "Masik123!"
        )

        assertFalse(result)
    }
}