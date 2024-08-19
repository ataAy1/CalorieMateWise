package com.app.meal_planning.di

import com.app.meal_planning.data.mapper.MealsMapper
import com.app.meal_planning.data.remote.MealPlanningApi
import com.app.meal_planning.data.repository.MealPlanningRepositoryImpl
import com.app.meal_planning.domain.repository.MealPlanningRepository
import com.app.meal_planning.domain.usecase.UploadMealPlanUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideMealPlanningRepository(
       repositoryImpl: MealPlanningRepositoryImpl
    ): MealPlanningRepository {
        return repositoryImpl
    }


}



