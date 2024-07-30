package com.app.signin.domain.repository

interface SignInRepository {
    suspend fun signIn(email: String, password: String)
}