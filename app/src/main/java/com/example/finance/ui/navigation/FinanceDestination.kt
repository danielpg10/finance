package com.example.finance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

object FinanceDestination {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val STATS = "stats"
    const val HISTORY = "history"
    const val FUNDS = "funds"
    const val ADD_TRANSACTION = "add_transaction"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(FinanceDestination.HOME, "Inicio", Icons.Rounded.Home),
    BottomNavItem(FinanceDestination.STATS, "Análisis", Icons.Rounded.DonutLarge),
    BottomNavItem(FinanceDestination.HISTORY, "Historial", Icons.Rounded.Receipt),
    BottomNavItem(FinanceDestination.FUNDS, "Fondos", Icons.Rounded.AccountBalanceWallet)
)
