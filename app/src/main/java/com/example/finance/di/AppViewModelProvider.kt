package com.example.finance.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finance.FinanceApplication
import com.example.finance.ui.screens.funds.FundsViewModel
import com.example.finance.ui.screens.history.HistoryViewModel
import com.example.finance.ui.screens.home.HomeViewModel
import com.example.finance.ui.screens.onboarding.OnboardingViewModel
import com.example.finance.ui.screens.stats.StatsViewModel
import com.example.finance.ui.screens.transaction.AddTransactionViewModel

object AppViewModelProvider {

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            OnboardingViewModel(
                container().userPreferencesRepository,
                container().fundRepository,
                container().goalRepository
            )
        }
        initializer {
            HomeViewModel(
                container().userPreferencesRepository,
                container().fundRepository,
                container().goalRepository,
                container().transactionRepository
            )
        }
        initializer {
            AddTransactionViewModel(
                container().categoryRepository,
                container().fundRepository,
                container().transactionRepository
            )
        }
        initializer {
            StatsViewModel(
                container().transactionRepository
            )
        }
        initializer {
            HistoryViewModel(
                container().transactionRepository
            )
        }
        initializer {
            FundsViewModel(
                container().fundRepository
            )
        }
    }

    private fun CreationExtras.container(): AppContainer =
        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FinanceApplication).container
}
