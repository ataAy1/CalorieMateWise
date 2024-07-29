package com.app.signup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.domain.model.User
import com.app.signup.domain.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState(isLoading = true)
            try {
                val userUid ="4214"
                val user = User(email, password)
                signUpUseCase.execute(userUid, user)
                _uiState.value = SignUpUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = SignUpUiState(error = e.message)
            }
        }
    }
}