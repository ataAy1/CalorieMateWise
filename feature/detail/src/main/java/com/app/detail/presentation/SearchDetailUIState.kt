package com.app.detail.presentation

data class SearchDetailUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
