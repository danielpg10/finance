package com.example.finance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
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
    BottomNavItem(FinanceDestination.HOME, "Inicio", Icons.Outlined.Home),
    BottomNavItem(FinanceDestination.STATS, "Análisis", Icons.Outlined.DonutLarge),
    BottomNavItem(FinanceDestination.HISTORY, "Historial", Icons.Outlined.Receipt),
    BottomNavItem(FinanceDestination.FUNDS, "Fondos", Icons.Outlined.AccountBalanceWallet)
)
