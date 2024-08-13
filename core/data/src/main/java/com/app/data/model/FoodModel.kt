package com.app.core.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable

data class FoodModel(
    @DocumentId val id: String? = null,
    val label: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbohydrates: Int = 0,
    val image: String = "",
    val date: String = "",
    val year: String = "",
    val yearOfMonth: String = "",
    val dayOfMonth: String = "",
    val dayName: String = ""
) : Serializable

@Parcelize
data class FoodModelParcelize(
    @DocumentId val id: String? = null,
    val label: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbohydrates: Int = 0,
    val image: String = "",
    val date: String = "",
    val year: String = "",
    val yearOfMonth: String = "",
    val dayOfMonth: String = "",
    val dayName: String = ""
) : Parcelable
