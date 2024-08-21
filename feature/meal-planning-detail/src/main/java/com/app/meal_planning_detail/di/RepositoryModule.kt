package com.app.meal_planning_detail.di


import com.app.meal_planning_detail.data.repository.MealPlansRepositoryImpl
import com.app.meal_planning_detail.domain.repository.MealPlansRepository
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
    fun provideMealPlanningRepository(
        repositoryImpl: MealPlansRepositoryImpl
    ): MealPlansRepository {
        return repositoryImpl
    }
}
