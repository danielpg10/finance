package com.example.finance.data.repository

import com.example.finance.data.local.dao.GoalDao
import com.example.finance.data.local.entity.GoalEntity
import com.example.finance.domain.model.Goal
import com.example.finance.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomGoalRepository(private val goalDao: GoalDao) : GoalRepository {

    override fun observeGoal(): Flow<Goal?> =
        goalDao.observeGoal().map { entity ->
            entity?.let { Goal(name = it.name, targetAmount = it.targetAmount) }
        }

    override suspend fun saveGoal(name: String, targetAmount: Long) {
        goalDao.upsert(GoalEntity(name = name, targetAmount = targetAmount))
    }
}
