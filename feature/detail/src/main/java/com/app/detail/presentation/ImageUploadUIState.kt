package com.app.detail.presentation

data class ImageUploadUIState(
    val isLoading: Boolean = false,
    val imageUrl: String? = null,
    val error: String? = null
)
