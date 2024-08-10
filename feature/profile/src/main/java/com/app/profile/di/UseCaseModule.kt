package com.app.profile.di

import com.app.profile.domain.repository.ProfileRepository
import com.app.profile.domain.usecase.FetchFoodListUseCase
import com.app.profile.domain.usecase.FetchUserInfoUseCase
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
    fun provideFetchFoodListUseCase(
        profileRepository: ProfileRepository
    ): FetchFoodListUseCase {
        return FetchFoodListUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideFetchUserInfoUseCase(
        profileRepository: ProfileRepository
    ): FetchUserInfoUseCase {
        return FetchUserInfoUseCase(profileRepository)
    }
}
