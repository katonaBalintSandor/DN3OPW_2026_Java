package com.example.dn3opw_2026

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimpleLeaseLogicTest {

    private fun canLease(quantity: Int): Boolean {
        return quantity > 0
    }

    @Test
    fun lease_is_possible_when_quantity_positive() {
        assertTrue(canLease(2))
    }

    @Test
    fun lease_is_not_possible_when_quantity_zero() {
        assertFalse(canLease(0))
    }
}