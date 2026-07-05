package com.example.finance.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.domain.model.FundType
import com.example.finance.domain.repository.FundRepository
import com.example.finance.domain.repository.GoalRepository
import com.example.finance.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DraftFund(
    val name: String,
    val type: FundType,
    val initialBalance: Long
)

data class OnboardingUiState(
    val step: Int = 0,
    val name: String = "",
    val goalName: String = "",
    val goalTarget: String = "",
    val funds: List<DraftFund> = emptyList(),
    val finished: Boolean = false
) {
    val canContinueName: Boolean
        get() = name.isNotBlank()

    val canContinueGoal: Boolean
        get() = goalName.isNotBlank() && (goalTarget.toLongOrNull() ?: 0L) > 0L
}

class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val fundRepository: FundRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }

    fun onGoalNameChange(value: String) = _uiState.update { it.copy(goalName = value) }

    fun onGoalTargetChange(value: String) =
        _uiState.update { it.copy(goalTarget = value.filter(Char::isDigit).take(12)) }

    fun addFund(name: String, type: FundType, initialBalance: Long) =
        _uiState.update { it.copy(funds = it.funds + DraftFund(name, type, initialBalance)) }

    fun removeFund(index: Int) =
        _uiState.update { it.copy(funds = it.funds.filterIndexed { i, _ -> i != index }) }

    fun nextStep() = _uiState.update { it.copy(step = it.step + 1) }

    fun previousStep() = _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }

    fun finish() {
        val state = _uiState.value
        viewModelScope.launch {
            userPreferencesRepository.saveUserName(state.name.trim())
            goalRepository.saveGoal(state.goalName.trim(), state.goalTarget.toLongOrNull() ?: 0L)
            state.funds.ifEmpty {
                listOf(DraftFund("Mis ahorros", FundType.CASH, 0L))
            }.forEach { fund ->
                fundRepository.addFund(
                    name = fund.name,
                    type = fund.type.name,
                    initialBalance = fund.initialBalance,
                    includeInGoal = true
                )
            }
            userPreferencesRepository.setOnboardingCompleted()
            _uiState.update { it.copy(finished = true) }
        }
    }
}
