package com.app.signup.domain.repository

import com.app.domain.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

interface FirebaseRepository {

    suspend fun saveUser(userUid: String, user: User): DocumentReference?

}