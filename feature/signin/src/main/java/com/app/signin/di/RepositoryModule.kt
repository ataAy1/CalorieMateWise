package com.app.signin.di

import com.app.signin.data.SignInRepositoryImpl
import com.app.signin.domain.repository.SignInRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSignInRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): SignInRepository = SignInRepositoryImpl(firebaseAuth, firebaseFirestore)
}
