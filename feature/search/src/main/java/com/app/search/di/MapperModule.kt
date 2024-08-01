package com.app.search.di

import com.app.search.data.mapper.SearchMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideSearchMapper(): SearchMapper {
        return SearchMapper()
    }
}
