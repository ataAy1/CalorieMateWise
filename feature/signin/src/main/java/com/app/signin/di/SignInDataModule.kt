package com.app.signin.di

import com.app.signin.domain.repository.SignInRepository
import com.app.signin.domain.usecase.SignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SignUpModule {

    @Provides
    @Singleton
    fun provideSignInUseCase(
        signInRepository: SignInRepository
    ): SignInUseCase = SignInUseCase(signInRepository)
}