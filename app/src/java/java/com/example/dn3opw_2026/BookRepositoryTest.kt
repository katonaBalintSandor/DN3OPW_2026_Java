package com.example.dn3opw_2026

import org.junit.Assert.*
import org.junit.Test

class BookRepositoryTest {

    private fun canLease(quantity: Int): Boolean {
        return quantity > 0
    }

    @Test
    fun lease_possible_when_in_stock() {
        assertTrue(canLease(3))
    }

    @Test
    fun lease_not_possible_when_zero() {
        assertFalse(canLease(0))
    }
}