package com.app.profile.domain.usecase

import com.app.profile.domain.repository.ProfileRepository
import com.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchUserInfoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    fun execute(): Flow<User> = profileRepository.getUserInfo()
}
