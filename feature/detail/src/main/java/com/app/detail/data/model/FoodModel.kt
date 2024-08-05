package com.app.detail.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class FoodModel(
    @DocumentId val id: String? = null,
    val label: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val image: String,
    val date: String,
    val year:String,
    val yearOfMonth:String,
    val dayOfMonth:String,
    val dayName: String
)
