package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import com.example.finance.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

data class TransactionWithRefs(
    @Embedded val transaction: TransactionEntity,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryColor: Long?,
    val fundName: String?
)

data class PeriodTotals(
    val income: Long,
    val expense: Long,
    val saving: Long
)

data class CategoryTotal(
    val categoryId: Long,
    val total: Long
)

@Dao
interface TransactionDao {

    @Query(
        """
        SELECT t.*, c.name AS categoryName, c.icon AS categoryIcon, c.color AS categoryColor, f.name AS fundName
        FROM transactions t
        LEFT JOIN categories c ON c.id = t.categoryId
        LEFT JOIN funds f ON f.id = t.fundId
        ORDER BY t.epochDay DESC, t.id DESC
        LIMIT :limit
        """
    )
    fun observeRecent(limit: Int): Flow<List<TransactionWithRefs>>

    @Query(
        """
        SELECT t.*, c.name AS categoryName, c.icon AS categoryIcon, c.color AS categoryColor, f.name AS fundName
        FROM transactions t
        LEFT JOIN categories c ON c.id = t.categoryId
        LEFT JOIN funds f ON f.id = t.fundId
        WHERE t.epochDay BETWEEN :startDay AND :endDay
        ORDER BY t.epochDay DESC, t.id DESC
        """
    )
    fun observeBetween(startDay: Long, endDay: Long): Flow<List<TransactionWithRefs>>

    @Query(
        """
        SELECT
            IFNULL(SUM(CASE WHEN type = 'INCOME' THEN amount END), 0) AS income,
            IFNULL(SUM(CASE WHEN type = 'EXPENSE' THEN amount END), 0) AS expense,
            IFNULL(SUM(CASE WHEN type = 'SAVING' THEN amount END), 0) AS saving
        FROM transactions
        WHERE epochDay BETWEEN :startDay AND :endDay
        """
    )
    fun observeTotalsBetween(startDay: Long, endDay: Long): Flow<PeriodTotals>

    @Query(
        """
        SELECT categoryId, SUM(amount) AS total
        FROM transactions
        WHERE type = 'EXPENSE' AND categoryId IS NOT NULL AND epochDay BETWEEN :startDay AND :endDay
        GROUP BY categoryId
        ORDER BY total DESC
        """
    )
    fun observeCategoryTotalsBetween(startDay: Long, endDay: Long): Flow<List<CategoryTotal>>

    @Query("SELECT MIN(epochDay) FROM transactions")
    fun observeFirstEpochDay(): Flow<Long?>

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun delete(transactionId: Long)
}
