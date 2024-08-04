package com.app.data.dto

import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class ParsedFood(
    val foodId: String,
    val label: String,
    val knownAs: String,
    val nutrients: Nutrients,
    val category: String,
    val categoryLabel: String,
    val image: String
) : Serializable

data class Nutrients(
    val ENERC_KCAL: Double,
    val PROCNT: Double,
    val FAT: Double,
    val CHOCDF: Double,
    val FIBTG: Double
): Serializable

data class Food(
    val foodId: String,
    val label: String,
    val knownAs: String,
    val nutrients: Nutrients,
    val category: String,
    val categoryLabel: String,
    val image: String
)

data class Measure(
    val uri: String,
    val label: String,
    val weight: Double,
    val qualified: List<Qualified>? = null
)

data class Qualified(
    val qualifiers: List<Qualifier>
)

data class Qualifier(
    val uri: String,
    val label: String
)

data class Parsed(
    val food: Food
)

data class Hint(
    val food: Food,
    val measures: List<Measure>
)

data class ApiResponse(
    val text: String = "",
    val parsed: List<Parsed> = emptyList(),
    val hints: List<Hint> = emptyList()
)
