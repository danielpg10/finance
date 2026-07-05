package com.example.finance.ui.screens.funds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.model.Fund
import com.example.finance.domain.model.FundType
import com.example.finance.domain.repository.FundRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FundsUiState(
    val funds: List<Fund> = emptyList()
) {
    val totalBalance: Long
        get() = funds.sumOf { it.balance }

    val goalBalance: Long
        get() = funds.filter { it.includeInGoal }.sumOf { it.balance }
}

class FundsViewModel(
    private val fundRepository: FundRepository
) : ViewModel() {

    val uiState: StateFlow<FundsUiState> = fundRepository.observeFunds()
        .map { FundsUiState(funds = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FundsUiState())

    fun addFund(name: String, type: FundType, initialBalance: Long) {
        viewModelScope.launch {
            fundRepository.addFund(name, type.name, initialBalance, includeInGoal = true)
        }
    }

    fun updateFund(fund: Fund) {
        viewModelScope.launch {
            fundRepository.updateFund(fund)
        }
    }

    fun setIncludeInGoal(fundId: Long, include: Boolean) {
        viewModelScope.launch {
            fundRepository.setIncludeInGoal(fundId, include)
        }
    }

    fun deleteFund(fundId: Long) {
        viewModelScope.launch {
            fundRepository.deleteFund(fundId)
        }
    }
}
