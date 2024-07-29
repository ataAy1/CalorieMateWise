package com.app.data.di


import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /*  @Provides
     @Singleton
    fun provideFirebaseRepository(
         firebaseFirestore: FirebaseFirestore
     ): FirebaseRepository {
         return FirebaseRepositoryImpl(firebaseFirestore)
     }*/


}