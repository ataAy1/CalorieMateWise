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
        val year = today.year.toString()
        val month = today.monthValue.toString().padStart(2, '0')
        val day = today.dayOfMonth.toString().padStart(2, '0')
        val dayName = today.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))

        try {

            val userDocRef = firestore.collection("meals").document(userId)
            val userDocSnapshot = userDocRef.get().await()
            if (!userDocSnapshot.exists()) {
                userDocRef.set(mapOf("exists" to true)).await()
            }


            val yearDocRef = userDocRef.collection("years").document(year)
            val yearDocSnapshot = yearDocRef.get().await()
            if (!yearDocSnapshot.exists()) {
                yearDocRef.set(mapOf("exists" to true)).await()
            }

            val monthDocRef = yearDocRef.collection("months").document(month)
            val monthDocSnapshot = monthDocRef.get().await()
            if (!monthDocSnapshot.exists()) {
                monthDocRef.set(mapOf("exists" to true)).await()
            }

            val dayDocRef = monthDocRef.collection("dayofmonth").document(day)
            val dayDocSnapshot = dayDocRef.get().await()
            if (!dayDocSnapshot.exists()) {
                dayDocRef.set(mapOf("exists" to true)).await()
            }

            dayDocRef.collection("foods").add(food).await()
            Log.d("FirestoreDebug", "Food item added successfully")

        } catch (e: Exception) {
            Log.e("FirestoreError", "Error saving document", e)
            throw e
        }
    }
}
