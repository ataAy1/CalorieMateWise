package com.app.domain.model

import java.io.Serializable

data class User(
    val email: String? = null,
    val height: String? = null,
    val weight: String? = null,
    val age: String? = null,
    val gender: Gender? = null
) : Serializable

enum class Gender : Serializable {
    MALE,
    FEMALE
}
