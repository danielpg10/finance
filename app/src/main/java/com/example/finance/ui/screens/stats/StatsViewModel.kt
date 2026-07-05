package com.example.finance.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.model.CategorySpending
import com.example.finance.domain.model.MonthSummary
import com.example.finance.domain.repository.TransactionRepository
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class StatsUiState(
    val month: YearMonth = YearMonth.now(),
    val summary: MonthSummary = MonthSummary(0, 0, 0),
    val spending: List<CategorySpending> = emptyList(),
    val firstMonth: YearMonth = YearMonth.now()
) {
    val totalSpent: Long
        get() = spending.sumOf { it.total }

    val canGoBack: Boolean
        get() = month > firstMonth

    val canGoForward: Boolean
        get() = month < YearMonth.now()
}

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<StatsUiState> = selectedMonth
        .flatMapLatest { month ->
            combine(
                transactionRepository.observeMonthSummary(month),
                transactionRepository.observeCategorySpending(month),
                transactionRepository.observeFirstTransactionDate()
            ) { summary, spending, firstDate ->
                StatsUiState(
                    month = month,
                    summary = summary,
                    spending = spending,
                    firstMonth = firstDate?.let { YearMonth.from(it) } ?: YearMonth.now()
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())

    fun previousMonth() = selectedMonth.update { it.minusMonths(1) }

    fun nextMonth() = selectedMonth.update { it.plusMonths(1) }
}
