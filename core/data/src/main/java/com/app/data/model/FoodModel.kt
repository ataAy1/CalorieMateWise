package com.app.core.data.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class FoodModel(
    @DocumentId val id: String? = null,
    val label: String = "",
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val image: String = "",
    val date: String = "",
    val year: String = "",
    val yearOfMonth: String = "",
    val dayOfMonth: String = "",
    val dayName: String = ""
) : Serializable
