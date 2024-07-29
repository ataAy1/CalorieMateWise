package com.app.signup.presentation

data class SignUpUIState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
