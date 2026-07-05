package com.example.finance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.model.Goal
import com.example.finance.domain.model.MonthSummary
import com.example.finance.domain.model.Transaction
import com.example.finance.domain.repository.FundRepository
import com.example.finance.domain.repository.GoalRepository
import com.example.finance.domain.repository.TransactionRepository
import com.example.finance.domain.repository.UserPreferencesRepository
import java.time.YearMonth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val goal: Goal? = null,
    val goalBalance: Long = 0L,
    val monthSummary: MonthSummary = MonthSummary(0, 0, 0),
    val recentTransactions: List<Transaction> = emptyList()
) {
    val goalProgress: Float
        get() = goal?.takeIf { it.targetAmount > 0 }
            ?.let { goalBalance.toFloat() / it.targetAmount }
            ?.coerceIn(0f, 1f) ?: 0f

    val remainingToGoal: Long
        get() = goal?.let { (it.targetAmount - goalBalance).coerceAtLeast(0L) } ?: 0L
}

class HomeViewModel(
    userPreferencesRepository: UserPreferencesRepository,
    fundRepository: FundRepository,
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        userPreferencesRepository.userName,
        goalRepository.observeGoal(),
        fundRepository.observeGoalBalance(),
        transactionRepository.observeMonthSummary(YearMonth.now()),
        transactionRepository.observeRecent(limit = 6)
    ) { name, goal, balance, summary, recent ->
        HomeUiState(
            userName = name.orEmpty(),
            goal = goal,
            goalBalance = balance,
            monthSummary = summary,
            recentTransactions = recent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun updateGoal(name: String, targetAmount: Long) {
        viewModelScope.launch {
            goalRepository.saveGoal(name, targetAmount)
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId)
        }
    }
}
