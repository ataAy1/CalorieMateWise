package com.app.signup.di

import com.app.signup.data.FirebaseRepositoryImpl
import com.app.signup.domain.repository.FirebaseRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
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
    fun provideFirebaseRepository(
        firebaseFirestore: FirebaseFirestore
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(firebaseFirestore)
    }
}
