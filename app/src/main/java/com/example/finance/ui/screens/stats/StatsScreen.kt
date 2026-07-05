package com.example.finance.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finance.di.AppViewModelProvider
import com.example.finance.domain.model.CategorySpending
import com.example.finance.ui.components.CategoryVisuals
import com.example.finance.ui.components.DonutChart
import com.example.finance.ui.components.DonutSlice
import com.example.finance.ui.components.EmptyState
import com.example.finance.ui.components.MonthSelector
import com.example.finance.util.toMoney

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 130.dp)
    ) {
        item {
            MonthSelector(
                month = uiState.month,
                canGoBack = uiState.canGoBack,
                canGoForward = uiState.canGoForward,
                onPrevious = viewModel::previousMonth,
                onNext = viewModel::nextMonth,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
        if (uiState.spending.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.DonutLarge,
                    title = "Sin gastos este mes",
                    subtitle = "Cuando registres gastos verás aquí en qué se va tu dinero."
                )
            }
        } else {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    DonutChart(
                        slices = uiState.spending.map {
                            DonutSlice(value = it.total.toFloat(), color = Color(it.category.color))
                        },
                        modifier = Modifier
                            .size(220.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 20.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Gastado",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = uiState.totalSpent.toMoney(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Por categoría",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
                )
            }
            items(uiState.spending.size) { index ->
                CategorySpendingRow(
                    spending = uiState.spending[index],
                    totalSpent = uiState.totalSpent
                )
            }
        }
    }
}

@Composable
private fun CategorySpendingRow(
    spending: CategorySpending,
    totalSpent: Long
) {
    val category = spending.category
    val categoryColor = Color(category.color)
    val percent = if (totalSpent > 0) spending.total * 100 / totalSpent else 0
    val budget = category.monthlyBudget

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(categoryColor.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = CategoryVisuals.icon(category.icon),
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = spending.total.toMoney(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (budget != null && budget > 0) {
                val fraction = (spending.total.toFloat() / budget).coerceIn(0f, 1f)
                val overBudget = spending.total > budget
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (overBudget) MaterialTheme.colorScheme.error else categoryColor
                )
                Text(
                    text = if (overBudget)
                        "Te pasaste del presupuesto de ${budget.toMoney()}"
                    else
                        "Presupuesto: ${budget.toMoney()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (overBudget)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "$percent% del gasto del mes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
