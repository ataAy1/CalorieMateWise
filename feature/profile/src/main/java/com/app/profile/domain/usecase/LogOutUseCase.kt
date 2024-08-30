package com.app.profile.domain.usecase


import com.app.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun execute() {
        profileRepository.logOut()
    }
}
