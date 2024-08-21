package com.app.meal_planning_detail.di

import com.app.meal_planning_detail.domain.repository.MealPlansRepository
import com.app.meal_planning_detail.domain.usecase.FetchMealsDetailUseCase
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
    fun provideGetMealPlansUseCase(repository: MealPlansRepository): FetchMealsDetailUseCase {
        return FetchMealsDetailUseCase(repository)
    }

}
