package com.example.finance.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finance.ui.navigation.FinanceDestination
import com.example.finance.ui.navigation.bottomNavItems
import com.example.finance.ui.screens.funds.FundsScreen
import com.example.finance.ui.screens.history.HistoryScreen
import com.example.finance.ui.screens.home.HomeScreen
import com.example.finance.ui.screens.onboarding.OnboardingScreen
import com.example.finance.ui.screens.stats.StatsScreen
import com.example.finance.ui.screens.transaction.AddTransactionScreen

@Composable
fun FinanceApp(startDestination: String) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showChrome = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showChrome) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showChrome) {
                FloatingActionButton(
                    onClick = { navController.navigate(FinanceDestination.ADD_TRANSACTION) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Agregar movimiento")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(FinanceDestination.ONBOARDING) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(FinanceDestination.HOME) {
                            popUpTo(FinanceDestination.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(FinanceDestination.HOME) { HomeScreen() }
            composable(FinanceDestination.STATS) { StatsScreen() }
            composable(FinanceDestination.HISTORY) { HistoryScreen() }
            composable(FinanceDestination.FUNDS) { FundsScreen() }
            composable(
                route = FinanceDestination.ADD_TRANSACTION,
                enterTransition = {
                    slideInVertically(animationSpec = tween(300)) { it } + fadeIn()
                },
                exitTransition = {
                    slideOutVertically(animationSpec = tween(300)) { it } + fadeOut()
                }
            ) {
                AddTransactionScreen(onClose = { navController.popBackStack() })
            }
        }
    }
}
