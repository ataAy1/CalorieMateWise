package com.app.profile.presentation

import com.app.domain.model.User

data class UserUIState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)