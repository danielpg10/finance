package com.example.finance.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finance.di.AppViewModelProvider
import com.example.finance.domain.model.Transaction
import com.example.finance.ui.components.EmptyState
import com.example.finance.ui.components.MonthSelector
import com.example.finance.ui.components.TransactionRow
import com.example.finance.ui.theme.ExpenseRed
import com.example.finance.ui.theme.IncomeGreen
import com.example.finance.ui.theme.SavingBlue
import com.example.finance.util.toMoney

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

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
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Resumen del mes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryLine("Ingresos", "+${uiState.summary.income.toMoney()}", IncomeGreen)
                    SummaryLine("Gastos", "-${uiState.summary.expense.toMoney()}", ExpenseRed)
                    SummaryLine("Aportes a la meta", uiState.summary.saving.toMoney(), SavingBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(
                            text = "Balance",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = uiState.summary.balance.toMoney(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (uiState.summary.balance >= 0)
                                IncomeGreen
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = "Movimientos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 4.dp)
            )
        }
        if (uiState.transactions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    title = "Nada por aquí",
                    subtitle = "Este mes no tiene movimientos registrados."
                )
            }
        } else {
            items(uiState.transactions.size) { index ->
                val transaction = uiState.transactions[index]
                TransactionRow(
                    transaction = transaction,
                    onLongPress = { transactionToDelete = transaction }
                )
            }
        }
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
private fun SummaryLine(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
