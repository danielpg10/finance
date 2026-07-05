package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.finance.data.local.entity.FundEntity
import kotlinx.coroutines.flow.Flow

data class FundWithBalance(
    @Embedded val fund: FundEntity,
    val balance: Long
)

@Dao
interface FundDao {

    @Query(
        """
        SELECT f.*, f.initialBalance + IFNULL(
            (SELECT SUM(t.amount) FROM transactions t WHERE t.fundId = f.id AND t.type = 'SAVING'), 0
        ) AS balance
        FROM funds f
        ORDER BY f.id
        """
    )
    fun observeFundsWithBalance(): Flow<List<FundWithBalance>>

    @Query(
        """
        SELECT IFNULL(SUM(f.initialBalance), 0) + IFNULL(
            (SELECT SUM(t.amount) FROM transactions t
             WHERE t.type = 'SAVING'
             AND t.fundId IN (SELECT id FROM funds WHERE includeInGoal = 1)), 0
        )
        FROM funds f WHERE f.includeInGoal = 1
        """
    )
    fun observeGoalBalance(): Flow<Long>

    @Insert
    suspend fun insert(fund: FundEntity)

    @Update
    suspend fun update(fund: FundEntity)

    @Query("UPDATE funds SET includeInGoal = :include WHERE id = :fundId")
    suspend fun setIncludeInGoal(fundId: Long, include: Boolean)

    @Query("DELETE FROM funds WHERE id = :fundId")
    suspend fun delete(fundId: Long)
}
