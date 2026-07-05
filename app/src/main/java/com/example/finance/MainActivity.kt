package com.example.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.example.finance.ui.FinanceApp
import com.example.finance.ui.navigation.FinanceDestination
import com.example.finance.ui.theme.FinanceTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferences = (application as FinanceApplication).container.userPreferencesRepository
        setContent {
            FinanceTheme {
                val startDestination by produceState<String?>(initialValue = null) {
                    value = if (preferences.onboardingCompleted.first())
                        FinanceDestination.HOME
                    else
                        FinanceDestination.ONBOARDING
                }
                startDestination?.let { FinanceApp(startDestination = it) }
            }
        }
    }
}
