package com.example.finance.data.repository

import com.example.finance.data.local.dao.FundDao
import com.example.finance.data.local.entity.FundEntity
import com.example.finance.domain.model.Fund
import com.example.finance.domain.model.FundType
import com.example.finance.domain.repository.FundRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFundRepository(private val fundDao: FundDao) : FundRepository {

    override fun observeFunds(): Flow<List<Fund>> =
        fundDao.observeFundsWithBalance().map { rows ->
            rows.map { row ->
                Fund(
                    id = row.fund.id,
                    name = row.fund.name,
                    type = FundType.valueOf(row.fund.type),
                    initialBalance = row.fund.initialBalance,
                    includeInGoal = row.fund.includeInGoal,
                    balance = row.balance
                )
            }
        }

    override fun observeGoalBalance(): Flow<Long> = fundDao.observeGoalBalance()

    override suspend fun addFund(name: String, type: String, initialBalance: Long, includeInGoal: Boolean) {
        fundDao.insert(
            FundEntity(
                name = name,
                type = type,
                initialBalance = initialBalance,
                includeInGoal = includeInGoal
            )
        )
    }

    override suspend fun updateFund(fund: Fund) {
        fundDao.update(
            FundEntity(
                id = fund.id,
                name = fund.name,
                type = fund.type.name,
                initialBalance = fund.initialBalance,
                includeInGoal = fund.includeInGoal
            )
        )
    }

    override suspend fun setIncludeInGoal(fundId: Long, include: Boolean) {
        fundDao.setIncludeInGoal(fundId, include)
    }

    override suspend fun deleteFund(fundId: Long) {
        fundDao.delete(fundId)
    }
}
