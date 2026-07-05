package com.example.finance.domain.repository

import com.example.finance.domain.model.CategorySpending
import com.example.finance.domain.model.MonthSummary
import com.example.finance.domain.model.Transaction
import com.example.finance.domain.model.TransactionType
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeRecent(limit: Int): Flow<List<Transaction>>
    fun observeMonth(month: YearMonth): Flow<List<Transaction>>
    fun observeMonthSummary(month: YearMonth): Flow<MonthSummary>
    fun observeCategorySpending(month: YearMonth): Flow<List<CategorySpending>>
    fun observeFirstTransactionDate(): Flow<LocalDate?>
    suspend fun addTransaction(
        amount: Long,
        type: TransactionType,
        categoryId: Long?,
        fundId: Long?,
        note: String?,
        date: LocalDate
    )
    suspend fun deleteTransaction(transactionId: Long)
}
