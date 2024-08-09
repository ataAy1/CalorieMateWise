package com.app.profile.data.repository

import com.app.core.data.model.FoodModel
import com.app.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    override fun getAllFoods(): Flow<List<FoodModel>> = flow {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            emit(emptyList())
            return@flow
        }

        val userId = user.uid
        val foodItems = mutableListOf<FoodModel>()

        try {
            val yearsSnapshot = firestore.collection("meals")
                .document(userId)
                .collection("years")
                .get()
                .await()

            val years = yearsSnapshot.documents.map { it.id }
            for (year in years) {
                val monthsSnapshot = firestore.collection("meals")
                    .document(userId)
                    .collection("years")
                    .document(year)
                    .collection("months")
                    .get()
                    .await()

                val months = monthsSnapshot.documents.map { it.id }
                for (month in months) {
                    val daysSnapshot = firestore.collection("meals")
                        .document(userId)
                        .collection("years")
                        .document(year)
                        .collection("months")
                        .document(month)
                        .collection("dayofmonth")
                        .get()
                        .await()

                    val days = daysSnapshot.documents.map { it.id }
                    for (day in days) {
                        val foodsSnapshot = firestore.collection("meals")
                            .document(userId)
                            .collection("years")
                            .document(year)
                            .collection("months")
                            .document(month)
                            .collection("dayofmonth")
                            .document(day)
                            .collection("foods")
                            .get()
                            .await()

                        val foods = foodsSnapshot.toObjects(FoodModel::class.java)
                        foodItems.addAll(foods)
                    }
                }
            }
            emit(foodItems)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
