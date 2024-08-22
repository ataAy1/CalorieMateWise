package com.app.search.presentation

import com.app.search.data.model.ParsedFood

data class SearchUIState(
    val combinedResponseState: List<com.app.data.dto.ParsedFood> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
