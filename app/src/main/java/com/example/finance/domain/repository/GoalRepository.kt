package com.example.finance.domain.repository

import com.example.finance.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeGoal(): Flow<Goal?>
    suspend fun saveGoal(name: String, targetAmount: Long)
}
