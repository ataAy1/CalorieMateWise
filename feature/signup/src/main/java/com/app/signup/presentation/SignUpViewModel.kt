package com.app.signup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.domain.model.User
import com.app.signup.domain.usecase.SignUpUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUIState())
    val uiState: StateFlow<SignUpUIState> = _uiState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUIState(isLoading = true)
            try {
                // Use Firebase Authentication to create the user
                val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
                val userUid = authResult.user?.uid ?: throw Exception("User UID is null")

                // Create a User object and pass it to the use case
                val user = User(email, password)
                signUpUseCase.execute(userUid, user)
                _uiState.value = SignUpUIState(success = true)
            } catch (e: Exception) {
                _uiState.value = SignUpUIState(error = e.message)
            }
        }
    }

}