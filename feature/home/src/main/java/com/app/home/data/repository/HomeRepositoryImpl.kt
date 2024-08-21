    package com.app.home.data.repository

    import android.content.ContentValues.TAG
    import android.util.Log
    import com.app.core.data.model.FoodModel
    import com.app.home.domain.repository.HomeRepository
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.flow
    import kotlinx.coroutines.tasks.await
    import java.text.SimpleDateFormat
    import java.time.LocalDate
    import java.time.format.DateTimeFormatter
    import java.util.Date
    import java.util.Locale
    import javax.inject.Inject
    import com.google.firebase.firestore.CollectionReference
    import com.google.firebase.firestore.Query

    class HomeRepositoryImpl @Inject constructor(
        private val firestore: FirebaseFirestore
    ) : HomeRepository {

        override fun getTodayFoods(): Flow<List<FoodModel>> = flow {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.e("FirestoreError", "User not authenticated")
                emit(emptyList())
                return@flow
            }
            val userId = user.uid
            val date = LocalDate.now()

            val year = date.year.toString()
            val yearMonth = date.monthValue.toString().padStart(2, '0')
            val dayOfMonth = date.dayOfMonth.toString().padStart(2, '0')
            val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))

            Log.e("FirestoreDebug", "Date: $year-$yearMonth-$dayOfMonth ($dayName)")

            try {
                val querySnapshot = firestore.collection("meals")
                    .document(userId)
                    .collection("years")
                    .document(year)
                    .collection("months")
                    .document(yearMonth)
                    .collection("dayofmonth")
                    .document(dayOfMonth)
                    .collection("foods")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val foods = querySnapshot.toObjects(FoodModel::class.java)
                Log.d("FirestoreSuccess", "Retrieved ${foods.size} food items")
                Log.d("FirestoreSuccess", "Food items: $foods")
                emit(foods)
            } catch (e: Exception) {
                Log.e("FirestoreError", "Error getting documents", e)
                emit(emptyList())
            }
        }

        override fun getAllFoods(): Flow<List<FoodModel>> = flow {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.e("FirestoreError", "User not authenticated")
                emit(emptyList())
                return@flow
            }

            val userId = user.uid
            val foodItems = mutableListOf<FoodModel>()

            try {
                Log.d("FirestoreDebug", "Fetching data for userId: $userId")

                val yearsSnapshot = firestore.collection("meals")
                    .document(userId)
                    .collection("years")
                    .get()
                    .await()

                val years = yearsSnapshot.documents.map { it.id }
                Log.d("FirestoreDebug", "Years found: $years")

                if (years.isEmpty()) {
                    Log.d("FirestoreDebug", "No years documents found")
                    emit(emptyList())
                    return@flow
                }

                for (year in years) {
                    val monthsSnapshot = firestore.collection("meals")
                        .document(userId)
                        .collection("years")
                        .document(year)
                        .collection("months")
                        .get()
                        .await()

                    val months = monthsSnapshot.documents.map { it.id }
                    Log.d("FirestoreDebug", "Months found for year $year: $months")

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
                        Log.d("FirestoreDebug", "Days found for month $month of year $year: $days")

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
                            Log.d("FirestoreDebug", "Foods found for day $day of month $month of year $year: $foods")
                            foodItems.addAll(foods)
                        }
                    }
                }

                emit(foodItems)
            } catch (e: Exception) {
                Log.e("FirestoreError", "Error getting food items", e)
                emit(emptyList())
            }
        }

    }