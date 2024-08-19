package com.app.meal_planning.di

import com.app.meal_planning.domain.repository.MealPlanningRepository
import com.app.meal_planning.domain.usecase.MealPlanningUseCase
import com.app.meal_planning.domain.usecase.UploadMealPlanUseCase
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
    fun provideMealPlanningUseCase(repository: MealPlanningRepository): MealPlanningUseCase {
        return MealPlanningUseCase(repository)
    }

    @Provides
    @Singleton
    fun providUploadMealPlanUseCase(repository: MealPlanningRepository): UploadMealPlanUseCase {
        return UploadMealPlanUseCase(repository)
    }
}
