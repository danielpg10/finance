package com.example.finance.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finance.ui.components.FloatingNavBar
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

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
            if (showChrome) {
                FloatingNavBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAdd = { navController.navigate(FinanceDestination.ADD_TRANSACTION) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}
