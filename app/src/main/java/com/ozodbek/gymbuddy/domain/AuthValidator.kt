package com.ozodbek.gymbuddy.domain

/** Pure functions (no Android classes) so they can be unit-tested on the JVM. Each returns an error message or null. */
object AuthValidator {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !emailRegex.matches(email.trim()) -> "Enter a valid email address"
        else -> null
    }

    fun validatePassword(password: String): String? = when {
        password.isEmpty() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }

    fun validatePasswordConfirmation(password: String, confirmation: String): String? = when {
        confirmation.isEmpty() -> "Please confirm your password"
        password != confirmation -> "Passwords do not match"
        else -> null
    }
}
