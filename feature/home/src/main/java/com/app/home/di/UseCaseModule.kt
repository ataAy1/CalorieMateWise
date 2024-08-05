package com.app.home.di

import com.app.home.domain.repository.HomeRepository
import com.app.home.domain.usecase.GetFoodsByDateUseCase
import com.app.home.domain.usecase.GetTodayFoodsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetTodayFoodsUseCase(
        homeRepository: HomeRepository
    ): GetTodayFoodsUseCase {
        return GetTodayFoodsUseCase(homeRepository)
    }

    @Provides
    @Singleton
    fun provideGetFoodsByDateUseCase(
        homeRepository: HomeRepository
    ): GetFoodsByDateUseCase {
        return GetFoodsByDateUseCase(homeRepository)
    }
}
