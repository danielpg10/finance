package com.example.finance.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.finance.data.local.FinanceDatabase
import com.example.finance.data.preferences.DataStoreUserPreferencesRepository
import com.example.finance.data.repository.RoomCategoryRepository
import com.example.finance.data.repository.RoomFundRepository
import com.example.finance.data.repository.RoomGoalRepository
import com.example.finance.data.repository.RoomTransactionRepository
import com.example.finance.domain.repository.CategoryRepository
import com.example.finance.domain.repository.FundRepository
import com.example.finance.domain.repository.GoalRepository
import com.example.finance.domain.repository.TransactionRepository
import com.example.finance.domain.repository.UserPreferencesRepository

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class AppContainer(context: Context) {

    private val database = FinanceDatabase.build(context)

    val userPreferencesRepository: UserPreferencesRepository =
        DataStoreUserPreferencesRepository(context.dataStore)

    val fundRepository: FundRepository =
        RoomFundRepository(database.fundDao())

    val categoryRepository: CategoryRepository =
        RoomCategoryRepository(database.categoryDao())

    val transactionRepository: TransactionRepository =
        RoomTransactionRepository(database.transactionDao(), database.categoryDao())

    val goalRepository: GoalRepository =
        RoomGoalRepository(database.goalDao())
}
