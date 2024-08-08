package com.app.profile.di

import com.app.profile.data.repository.ProfileRepositoryImpl
import com.app.profile.domain.repository.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileRepositoryModule {


    @Provides
    @Singleton
    fun provideProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository {
        return profileRepositoryImpl
    }
}
