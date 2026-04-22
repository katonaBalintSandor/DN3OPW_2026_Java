package com.example.dn3opw_2026.validation

object EventValidator {
    private val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")

    fun isValidDate(date: String): Boolean {
        return dateRegex.matches(date)
    }
}