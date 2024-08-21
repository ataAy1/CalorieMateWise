package com.app.meal_planning.data.repository


import android.content.Context
import android.net.Uri
import android.util.Log
import com.app.meal_planning.data.mapper.MealsMapper
import com.app.meal_planning.data.model.MealPlanUpload
import com.app.meal_planning.data.remote.MealPlanningApi
import com.app.meal_planning.domain.repository.MealPlanningRepository
import com.app.meal_planning.dto.MealPlanRequest
import com.app.meal_planning.data.model.MealsModel
import com.app.meal_planning.dto.RecipeResponse
import com.app.utils.DateUtil
import com.app.utils.ImageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import retrofit2.Response
import java.util.UUID

class MealPlanningRepositoryImpl @Inject constructor(
    private val api: MealPlanningApi,
    private val mapper: MealsMapper,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : MealPlanningRepository {

    override suspend fun getMealPlan(request: MealPlanRequest): MealsModel {
        Log.d("MealPlanningRepositoryImpl3", "Sending request: $request")
        val response = api.getMealPlan(
            appId = "490834e2",
            userId = "micheal708",
            authHeader = "Basic NDkwODM0ZTI6NGRiYmI4ZjY2YjlmMDk1ODJjOGRkMmQ3OGNjMTcyOGY=",
            request = request
        )

        if (response.isSuccessful) {
            val responseBody = response.body() ?: throw Exception("Response body is null")
            Log.d("MealPlanningRepositoryImpl1", "Response received: $responseBody")
            return mapper.mapToMealsModel(responseBody)
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            Log.e("MealPlanningRepositoryImpl2", "API call failed with code ${response.code()}: $errorBody")
            throw Exception("API call failed with code ${response.code()}: $errorBody")
        }

    }

    override suspend fun getRecipeByUri(uri: String): RecipeResponse {
        Log.d("MealPlanningRepositoryImpl4", "Fetching recipe details for URI: $uri")
        val fieldsArray = listOf("label", "image", "calories", "mealType","yield")

        val response = api.getRecipeByUri(
            type = "public",
            beta = true,
            uri = uri,
            appId = "490834e2",
            appKey = "4dbbb8f66b9f09582c8dd2d78cc1728f",
            fields = fieldsArray
        )

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
            Log.e("ResponseBody", "ResponseBody ${response.code()}: ${response.body()}")

        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            Log.e("MealPlanningRepositoryImpl5", "API call failed with code ${response.code()}: $errorBody")
            throw Exception("API call failed with code ${response.code()}: $errorBody")
        }
    }

    override suspend fun uploadMealPlans(mealPlans: List<MealPlanUpload>, context: Context) {
        val userId = auth.currentUser?.uid
        val currentDate = DateUtil.getCurrentDate()
        val uniqueMealSetId = UUID.randomUUID().toString()

        if (userId == null) {
            Log.e("uploadMealPlans", "User ID is null")
            return
        }

        val mealPlansRef = firestore.collection("mealPlanning")
            .document(userId)
            .collection("dates")
            .document(currentDate)
            .collection("mealSets")

        val mealSetDocRef = mealPlansRef.document(uniqueMealSetId)

        try {
            val dateDocRef = firestore.collection("mealPlanning")
                .document(userId)
                .collection("dates")
                .document(currentDate)

            val dateDocSnapshot = dateDocRef.get().await()
            if (!dateDocSnapshot.exists()) {
                dateDocRef.set(mapOf("exists" to true)).await()
                Log.d("uploadMealPlans", "Date document created for: $currentDate")
            }

            val mealSetDocSnapshot = mealSetDocRef.get().await()
            if (!mealSetDocSnapshot.exists()) {
                mealSetDocRef.set(mapOf("exists" to true)).await()
                Log.d("uploadMealPlans", "Meal set document created for: $uniqueMealSetId")
            }

            val batch = firestore.batch()

            for (mealPlan in mealPlans) {
                val imageUri = mealPlan.imageUrl

                if (imageUri.isNotEmpty()) {
                    try {
                        val imageByteArray = ImageUtil.downloadImage(imageUri, context)

                        if (imageByteArray != null) {
                            val imageRef = storage.reference.child("mealPlanningImages/$userId/${Uri.parse(imageUri).lastPathSegment}")

                            val uploadTask = imageRef.putBytes(imageByteArray).await()

                            val imageDownloadUrl = uploadTask.metadata?.reference?.downloadUrl?.await().toString()

                            val mealPlanData = mealPlan.copy(imageUrl = imageDownloadUrl)

                            val mealPlanRef = mealSetDocRef.collection("meals").document()
                            batch.set(mealPlanRef, mealPlanData)
                        } else {
                            Log.e("uploadMealPlans", "Error downloading image from $imageUri")
                        }
                    } catch (e: Exception) {
                        Log.e("uploadMealPlans", "Error uploading image or meal plan: ", e)
                    }
                } else {
                    try {
                        val mealPlanData = mealPlan.copy(imageUrl = "")

                        val mealPlanRef = mealSetDocRef.collection("meals").document()
                        batch.set(mealPlanRef, mealPlanData)
                    } catch (e: Exception) {
                        Log.e("uploadMealPlans", "Error uploading meal plan: ", e)
                    }
                }
            }

            batch.commit().await()
            Log.d("uploadMealPlans", "All meal plans uploaded successfully")

        } catch (e: Exception) {
            Log.e("uploadMealPlans", "Error uploading meal plans", e)
            throw e
        }
    }

    override suspend fun getUserCount(): String {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val userDocRef = firestore.collection("Users").document(userId)
            val userDocSnapshot = userDocRef.get().await()

            if (userDocSnapshot.exists()) {
                val count = userDocSnapshot.getString("count") ?: "0"
                Log.d("fetchUserCount", "User count fetched: $count")
                count
            } else {
                "0"
            }
        } catch (e: Exception) {
            Log.e("fetchUserCount", "Error fetching user count", e)
            "0"
        }
    }


    override suspend fun updateUserCount(newCountDate: String) {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val userDocRef = firestore.collection("Users").document(userId)

            val userDocSnapshot = userDocRef.get().await()
            if (!userDocSnapshot.exists()) {
                userDocRef.set(mapOf("count" to newCountDate)).await()
                Log.d("fetchUserCount", "User document created and count set to $newCountDate")
            } else {
                userDocRef.update("count", newCountDate).await()
                Log.d("fetchUserCount", "User count updated to $newCountDate")
            }
        } catch (e: Exception) {
            Log.e("fetchUserCount", "Error updating user count", e)
            throw e
        }
    }


}