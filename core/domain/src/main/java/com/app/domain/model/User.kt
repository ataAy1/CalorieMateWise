package com.app.domain.model

import java.io.Serializable

data class User(
    val email: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val age: Int? = null,
    val gender: Gender? = null
) : Serializable

enum class Gender : Serializable {
    MALE,
    FEMALE
}
