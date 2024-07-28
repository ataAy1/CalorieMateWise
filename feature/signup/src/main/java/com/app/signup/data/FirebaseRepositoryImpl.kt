package com.app.signup.data

import com.app.domain.model.User
import com.app.signup.domain.repository.FirebaseRepository
import com.app.signup.util.Constants.USER_COLLECTION
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    ): FirebaseRepository {
    override suspend fun saveUser(
        userUid: String, user: User
    ): Task<DocumentReference?> {
        return firebaseFirestore.collection(USER_COLLECTION).document(userUid).collection("info").add(user)
    }
}