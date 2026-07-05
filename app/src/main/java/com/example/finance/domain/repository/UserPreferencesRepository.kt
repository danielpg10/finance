package com.example.finance.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userName: Flow<String?>
    val onboardingCompleted: Flow<Boolean>
    suspend fun saveUserName(name: String)
    suspend fun setOnboardingCompleted()
}
