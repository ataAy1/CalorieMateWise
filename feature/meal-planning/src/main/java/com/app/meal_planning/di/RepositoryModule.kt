package com.app.meal_planning.di

import com.app.meal_planning.data.mapper.MealsMapper
import com.app.meal_planning.data.remote.MealPlanningApi
import com.app.meal_planning.data.repository.MealPlanningRepositoryImpl
import com.app.meal_planning.domain.repository.MealPlanningRepository
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
    fun provideMealsMapper(): MealsMapper {
        return MealsMapper()
    }

    @Provides
    @Singleton
    fun provideMealPlanningRepository(
        api: MealPlanningApi,
        mapper: MealsMapper
    ): MealPlanningRepository {
        return MealPlanningRepositoryImpl(api, mapper)
    }
}
