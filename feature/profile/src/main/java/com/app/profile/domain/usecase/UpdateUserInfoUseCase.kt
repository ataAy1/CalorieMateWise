package com.app.profile.domain.usecase

import com.app.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun execute(height: String, weight: String, age: String) {
        profileRepository.updateUserInfo(height, weight, age)
    }
}
