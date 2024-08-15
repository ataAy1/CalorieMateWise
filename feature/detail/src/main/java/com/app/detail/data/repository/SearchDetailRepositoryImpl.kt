package com.app.detail.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.app.detail.data.model.FoodModel
import com.app.detail.domain.repository.SearchDetailRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
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

    override suspend fun uploadImage(imageUri: Uri): String {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("User not authenticated")

        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/$userId/${System.currentTimeMillis()}.jpg")

        try {
            Log.d("UploadDebug", "Starting upload for Uri: $imageUri")
            val uploadTask = imageRef.putFile(imageUri).await()
            Log.d("UploadDebug", "Upload task completed with success: ${uploadTask.task.isSuccessful}")

            if (uploadTask.task.isSuccessful) {
                val downloadUrl = imageRef.downloadUrl.await().toString()
                Log.d("UploadDebug", "File uploaded successfully. Download URL: $downloadUrl")
                return downloadUrl
            } else {
                val exception = uploadTask.task.exception
                Log.e("UploadError", "Upload failed with exception: ${exception?.message}", exception)
                throw exception ?: Exception("Unknown error occurred during image upload")
            }
        } catch (e: StorageException) {
            Log.e("UploadError", "StorageException: ${e.message}", e)
            throw e
        } catch (e: Exception) {
            Log.e("UploadError", "Exception: ${e.message}", e)
            throw e
        }
    }

}