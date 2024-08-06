package com.app.detail.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.app.detail.data.model.FoodModel
import com.app.detail.domain.repository.SearchDetailRepository
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class SearchDetailRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SearchDetailRepository {

    override suspend fun addFoodToMeal(food: FoodModel) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("FirestoreError", "User not authenticated")
            throw Exception("User not authenticated")
        }

        val userId = user.uid
        val today = LocalDate.now()
        val dayName = today.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))

        try {
            firestore.collection("meals")
                .document(userId)
                .collection(today.year.toString())
                .document(today.monthValue.toString().padStart(2, '0'))
                .collection(today.dayOfMonth.toString().padStart(2, '0'))
                .add(food)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error saving document", e)
            throw e
        }
    }
}
