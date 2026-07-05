package com.example.finance.domain.repository

import com.example.finance.domain.model.Fund
import kotlinx.coroutines.flow.Flow

interface FundRepository {
    fun observeFunds(): Flow<List<Fund>>
    fun observeGoalBalance(): Flow<Long>
    suspend fun addFund(name: String, type: String, initialBalance: Long, includeInGoal: Boolean)
    suspend fun updateFund(fund: Fund)
    suspend fun setIncludeInGoal(fundId: Long, include: Boolean)
    suspend fun deleteFund(fundId: Long)
}
