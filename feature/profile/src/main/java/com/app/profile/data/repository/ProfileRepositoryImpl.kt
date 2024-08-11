    package com.app.profile.data.repository

    import android.util.Log
    import com.app.core.data.model.FoodModel
    import com.app.domain.model.User
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
        override fun getUserInfo(): Flow<User> = flow {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                emit(User())
                return@flow
            }

            val userId = user.uid

            try {
                val userInfoSnapshot = firestore.collection("Users")
                    .document(userId)
                    .collection("user_info")
                    .get()
                    .await()

                if (!userInfoSnapshot.isEmpty) {
                    val userDoc = userInfoSnapshot.documents[0]

                    val userInfo = userDoc.toObject(User::class.java) ?: User()

                    emit(userInfo)
                } else {
                    emit(User())
                }
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error fetching user info", e)
                emit(User())
            }

        }


        override suspend fun updateUserInfo(height: String, weight: String, age: String) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userId = user.uid
                try {
                    val userInfoSnapshot = firestore.collection("Users")
                        .document(userId)
                        .collection("user_info")
                        .get()
                        .await()

                    if (userInfoSnapshot.documents.isNotEmpty()) {
                        val docId = userInfoSnapshot.documents[0].id

                        firestore.collection("Users")
                            .document(userId)
                            .collection("user_info")
                            .document(docId)
                            .update(
                                mapOf(
                                    "height" to height,
                                    "weight" to weight,
                                    "age" to age
                                )
                            )
                            .await()
                    }
                } catch (e: Exception) {
                    Log.e("ProfileRepository", "Error updating user info", e)
                }
            }
        }
    }