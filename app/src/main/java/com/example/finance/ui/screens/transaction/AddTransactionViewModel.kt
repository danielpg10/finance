package com.example.finance.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.model.Category
import com.example.finance.domain.model.Fund
import com.example.finance.domain.model.TransactionType
import com.example.finance.domain.repository.CategoryRepository
import com.example.finance.domain.repository.FundRepository
import com.example.finance.domain.repository.TransactionRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategoryId: Long? = null,
    val selectedFundId: Long? = null,
    val note: String = "",
    val saved: Boolean = false
) {
    val amountValue: Long
        get() = amount.toLongOrNull() ?: 0L

    val canSave: Boolean
        get() = amountValue > 0L && when (type) {
            TransactionType.EXPENSE -> selectedCategoryId != null
            TransactionType.SAVING -> selectedFundId != null
            TransactionType.INCOME -> true
        }
}

class AddTransactionViewModel(
    categoryRepository: CategoryRepository,
    fundRepository: FundRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryRepository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val funds: StateFlow<List<Fund>> = fundRepository.observeFunds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onDigit(digit: Char) {
        _uiState.update { state ->
            val next = (state.amount + digit).trimStart('0')
            if (next.length > 12) state else state.copy(amount = next)
        }
    }

    fun onDeleteDigit() {
        _uiState.update { it.copy(amount = it.amount.dropLast(1)) }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onFundSelected(fundId: Long) {
        _uiState.update { it.copy(selectedFundId = fundId) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note.take(80)) }
    }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            transactionRepository.addTransaction(
                amount = state.amountValue,
                type = state.type,
                categoryId = state.selectedCategoryId.takeIf { state.type == TransactionType.EXPENSE },
                fundId = state.selectedFundId.takeIf { state.type == TransactionType.SAVING },
                note = state.note,
                date = LocalDate.now()
            )
            _uiState.update { it.copy(saved = true) }
        }
    }
}
