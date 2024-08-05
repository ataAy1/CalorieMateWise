package com.app.detail.data.repository

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
        val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("User not authenticated")
        val userId = user.uid
        val today = LocalDate.now()
        val yearMonth = today.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val day = today.dayOfMonth
        val dayName = today.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))

        try {
            firestore.collection("meals")
                .document(userId)
                .collection(food.year)
                .document(food.yearOfMonth)
                .collection(food.dayOfMonth.toString())
                .document(dayName)
                .collection("foods")
                .add(food)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}
