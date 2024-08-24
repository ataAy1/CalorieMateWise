package com.app.signin.domain.usecase

import com.app.signin.domain.repository.SignInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val signInRepository: SignInRepository
) {
    fun resetPassword(email: String): Flow<Result<Unit>> = flow {
        try {
            signInRepository.resetPassword(email)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
