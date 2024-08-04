package com.app.detail.di

import com.app.detail.data.repository.SearchDetailRepositoryImpl
import com.app.detail.domain.repository.SearchDetailRepository
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
    fun provideSearchDetailRepository(
        repositoryImpl: SearchDetailRepositoryImpl
    ): SearchDetailRepository {
        return repositoryImpl
    }
}