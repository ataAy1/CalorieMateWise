package com.app.signup.data

import com.app.signup.domain.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,

    )

    : AuthenticationRepository {


}