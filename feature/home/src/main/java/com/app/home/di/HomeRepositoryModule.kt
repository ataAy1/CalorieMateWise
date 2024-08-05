package com.app.home.di

import com.app.home.data.repository.HomeRepositoryImpl
import com.app.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeRepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository {
        return homeRepositoryImpl
    }
}
