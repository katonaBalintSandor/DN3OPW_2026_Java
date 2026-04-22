package com.example.dn3opw_2026

import com.example.dn3opw_2026.validation.EventValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EventValidatorTest {

    @Test
    fun valid_date_returns_true() {
        assertTrue(EventValidator.isValidDate("2026-04-17"))
    }

    @Test
    fun invalid_date_returns_false() {
        assertFalse(EventValidator.isValidDate("17/04/2026"))
    }

    @Test
    fun empty_date_returns_false() {
        assertFalse(EventValidator.isValidDate(""))
    }
}