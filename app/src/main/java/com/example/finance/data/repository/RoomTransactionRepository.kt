package com.example.finance.data.repository

import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.dao.TransactionDao
import com.example.finance.data.local.dao.TransactionWithRefs
import com.example.finance.data.local.entity.TransactionEntity
import com.example.finance.domain.model.CategorySpending
import com.example.finance.domain.model.MonthSummary
import com.example.finance.domain.model.Transaction
import com.example.finance.domain.model.TransactionType
import com.example.finance.domain.repository.TransactionRepository
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RoomTransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : TransactionRepository {

    override fun observeRecent(limit: Int): Flow<List<Transaction>> =
        transactionDao.observeRecent(limit).map { rows -> rows.map { it.toDomain() } }

    override fun observeMonth(month: YearMonth): Flow<List<Transaction>> =
        transactionDao.observeBetween(month.startDay(), month.endDay())
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeMonthSummary(month: YearMonth): Flow<MonthSummary> =
        transactionDao.observeTotalsBetween(month.startDay(), month.endDay())
            .map { MonthSummary(income = it.income, expense = it.expense, saving = it.saving) }

    override fun observeCategorySpending(month: YearMonth): Flow<List<CategorySpending>> =
        combine(
            transactionDao.observeCategoryTotalsBetween(month.startDay(), month.endDay()),
            categoryDao.observeAll()
        ) { totals, categories ->
            val byId = categories.associateBy { it.id }
            totals.mapNotNull { total ->
                byId[total.categoryId]?.let { CategorySpending(it.toDomain(), total.total) }
            }
        }

    override fun observeFirstTransactionDate(): Flow<LocalDate?> =
        transactionDao.observeFirstEpochDay().map { day -> day?.let(LocalDate::ofEpochDay) }

    override suspend fun addTransaction(
        amount: Long,
        type: TransactionType,
        categoryId: Long?,
        fundId: Long?,
        note: String?,
        date: LocalDate
    ) {
        transactionDao.insert(
            TransactionEntity(
                amount = amount,
                type = type.name,
                categoryId = categoryId,
                fundId = fundId,
                note = note?.takeIf { it.isNotBlank() },
                epochDay = date.toEpochDay()
            )
        )
    }

    override suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.delete(transactionId)
    }

    private fun YearMonth.startDay() = atDay(1).toEpochDay()

    private fun YearMonth.endDay() = atEndOfMonth().toEpochDay()

    private fun TransactionWithRefs.toDomain() = Transaction(
        id = transaction.id,
        amount = transaction.amount,
        type = TransactionType.valueOf(transaction.type),
        categoryId = transaction.categoryId,
        categoryName = categoryName,
        categoryIcon = categoryIcon,
        categoryColor = categoryColor,
        fundId = transaction.fundId,
        fundName = fundName,
        note = transaction.note,
        date = LocalDate.ofEpochDay(transaction.epochDay)
    )
}
