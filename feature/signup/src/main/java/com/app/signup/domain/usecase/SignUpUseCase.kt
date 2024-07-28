package com.app.signup.domain.usecase

import com.app.domain.model.User
import com.app.signup.domain.repository.FirebaseRepository
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend fun execute(userUid: String, user: User): DocumentReference? {
        return repository.saveUser(userUid, user)
    }
}