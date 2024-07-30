package com.app.signin.domain.usecase

import com.app.signin.domain.repository.SignInRepository

class SignInUseCase(
    private val signInRepository: SignInRepository
) {
    suspend fun execute(email: String, password: String) {
        signInRepository.signIn(email, password)
    }
}