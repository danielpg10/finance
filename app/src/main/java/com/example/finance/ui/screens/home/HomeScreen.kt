package com.example.finance.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finance.di.AppViewModelProvider
import com.example.finance.domain.model.Transaction
import com.example.finance.ui.components.EmptyState
import com.example.finance.ui.components.GoalProgressRing
import com.example.finance.ui.components.MoneyTextField
import com.example.finance.ui.components.TransactionRow
import com.example.finance.ui.theme.ExpenseRed
import com.example.finance.ui.theme.IncomeGreen
import com.example.finance.ui.theme.SavingBlue
import com.example.finance.util.toMoney

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showGoalDialog by rememberSaveable { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = if (uiState.userName.isBlank()) "Hola 👋" else "Hola, ${uiState.userName} 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)
            )
            Text(
                text = "Así va tu meta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        item {
            GoalCard(
                uiState = uiState,
                onEditGoal = { showGoalDialog = true }
            )
        }
        item {
            MonthSummaryRow(uiState = uiState)
        }
        item {
            Text(
                text = "Movimientos recientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 4.dp)
            )
        }
        if (uiState.recentTransactions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                    title = "Sin movimientos aún",
                    subtitle = "Toca el botón + para registrar tu primer gasto, ingreso o aporte."
                )
            }
        } else {
            items(uiState.recentTransactions.size) { index ->
                val transaction = uiState.recentTransactions[index]
                TransactionRow(
                    transaction = transaction,
                    onLongPress = { transactionToDelete = transaction }
                )
            }
        }
    }

    if (showGoalDialog) {
        EditGoalDialog(
            currentName = uiState.goal?.name.orEmpty(),
            currentTarget = uiState.goal?.targetAmount ?: 0L,
            onDismiss = { showGoalDialog = false },
            onSave = { name, target ->
                viewModel.updateGoal(name, target)
                showGoalDialog = false
            }
        )
    }

    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Eliminar movimiento") },
            text = { Text("¿Quieres eliminar este movimiento de ${transaction.amount.toMoney()}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(transaction.id)
                    transactionToDelete = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun GoalCard(
    uiState: HomeUiState,
    onEditGoal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.goal?.name ?: "Mi meta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Objetivo: ${(uiState.goal?.targetAmount ?: 0L).toMoney()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onEditGoal) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar meta")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            GoalProgressRing(
                progress = uiState.goalProgress,
                modifier = Modifier
                    .size(190.dp)
                    .align(Alignment.CenterHorizontally),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                progressColor = MaterialTheme.colorScheme.primary
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.goalBalance.toMoney(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${(uiState.goalProgress * 100).toInt()}% de la meta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (uiState.remainingToGoal == 0L && uiState.goal != null)
                    "🎉 ¡Lo lograste!"
                else
                    "Te faltan ${uiState.remainingToGoal.toMoney()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MonthSummaryRow(uiState: HomeUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryChip(
            label = "Ingresos",
            amount = uiState.monthSummary.income,
            color = IncomeGreen,
            modifier = Modifier.weight(1f)
        )
        SummaryChip(
            label = "Gastos",
            amount = uiState.monthSummary.expense,
            color = ExpenseRed,
            modifier = Modifier.weight(1f)
        )
        SummaryChip(
            label = "Ahorrado",
            amount = uiState.monthSummary.saving,
            color = SavingBlue,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryChip(
    label: String,
    amount: Long,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount.toMoney(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun EditGoalDialog(
    currentName: String,
    currentTarget: Long,
    onDismiss: () -> Unit,
    onSave: (String, Long) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(currentName) }
    var target by rememberSaveable { mutableStateOf(if (currentTarget > 0) currentTarget.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar meta") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                MoneyTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = "Monto objetivo"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.trim(), target.toLongOrNull() ?: 0L) },
                enabled = name.isNotBlank() && (target.toLongOrNull() ?: 0L) > 0L
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
