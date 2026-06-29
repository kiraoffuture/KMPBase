package com.kira.kmpbase.core.domain.validation

import com.kira.kmpbase.core.domain.model.ValidationErrorCode

object LoginCredentialsValidator {
    private val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    const val MIN_PASSWORD_LENGTH = 6

    fun validate(email: String, password: String): ValidationErrorCode? {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isEmpty()) {
            return ValidationErrorCode.EMAIL_REQUIRED
        }
        if (!emailPattern.matches(normalizedEmail)) {
            return ValidationErrorCode.INVALID_EMAIL
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            return ValidationErrorCode.PASSWORD_TOO_SHORT
        }
        return null
    }
}
