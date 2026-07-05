package com.example.finance.ui.screens.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finance.di.AppViewModelProvider
import com.example.finance.domain.model.TransactionType
import com.example.finance.ui.components.CategoryVisuals
import com.example.finance.ui.theme.ExpenseRed
import com.example.finance.ui.theme.IncomeGreen
import com.example.finance.ui.theme.SavingBlue
import com.example.finance.util.toMoney

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    onClose: () -> Unit,
    viewModel: AddTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val funds by viewModel.funds.collectAsStateWithLifecycle()
    var showCreateCategory by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onClose()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo movimiento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TypeSelector(
                selected = uiState.type,
                onSelect = viewModel::onTypeChange
            )

            AmountDisplay(
                amount = uiState.amountValue,
                type = uiState.type
            )

            when (uiState.type) {
                TransactionType.EXPENSE -> {
                    SectionTitle("Categoría")
                    CategoryGrid(
                        categories = categories.map { Triple(it.id, it.name, it) },
                        selectedId = uiState.selectedCategoryId,
                        onSelect = viewModel::onCategorySelected,
                        onCreateNew = { showCreateCategory = true }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FromSavingsRow(
                        checked = uiState.fromSavings,
                        onCheckedChange = viewModel::onFromSavingsChange
                    )
                    AnimatedVisibility(visible = uiState.fromSavings) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            FundChips(
                                funds = funds.map { it.id to it.name },
                                selectedId = uiState.selectedFundId,
                                onSelect = viewModel::onFundSelected
                            )
                        }
                    }
                }
                TransactionType.SAVING -> {
                    SectionTitle("¿A qué fondo va tu aporte?")
                    FundChips(
                        funds = funds.map { it.id to it.name },
                        selectedId = uiState.selectedFundId,
                        onSelect = viewModel::onFundSelected
                    )
                }
                TransactionType.INCOME -> Unit
            }

            Spacer(modifier = Modifier.height(18.dp))
            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nota (opcional)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))
            NumericKeypad(
                onDigit = viewModel::onDigit,
                onDelete = viewModel::onDeleteDigit
            )

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = viewModel::save,
                enabled = uiState.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCreateCategory) {
        CreateCategorySheet(
            onDismiss = { showCreateCategory = false },
            onSave = { name, icon, color ->
                viewModel.createCategory(name, icon, color)
                showCreateCategory = false
            }
        )
    }
}

@Composable
private fun TypeSelector(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit
) {
    val options = listOf(
        Triple(TransactionType.EXPENSE, "Gasto", ExpenseRed),
        Triple(TransactionType.INCOME, "Ingreso", IncomeGreen),
        Triple(TransactionType.SAVING, "Aporte", SavingBlue)
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (type, label, color) ->
            SegmentedButton(
                selected = selected == type,
                onClick = { onSelect(type) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = color.copy(alpha = 0.16f),
                    activeContentColor = color
                )
            ) {
                Text(label, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AmountDisplay(
    amount: Long,
    type: TransactionType
) {
    val amountColor by animateColorAsState(
        targetValue = when {
            amount == 0L -> MaterialTheme.colorScheme.onSurfaceVariant
            type == TransactionType.EXPENSE -> ExpenseRed
            type == TransactionType.INCOME -> IncomeGreen
            else -> SavingBlue
        },
        label = "amountColor"
    )
    Text(
        text = if (amount > 0) amount.toMoney() else "$0",
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.Center,
        color = amountColor,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 22.dp)
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryGrid(
    categories: List<Triple<Long, String, com.example.finance.domain.model.Category>>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onCreateNew: () -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (id, name, category) ->
            val categoryColor = Color(category.color)
            val selected = selectedId == id
            FilterChip(
                selected = selected,
                onClick = { onSelect(id) },
                label = { Text(name) },
                leadingIcon = {
                    Icon(
                        imageVector = CategoryVisuals.icon(category.icon),
                        contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else categoryColor,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = categoryColor.copy(alpha = 0.18f),
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                    selectedLeadingIconColor = categoryColor
                )
            )
        }
        FilterChip(
            selected = false,
            onClick = onCreateNew,
            label = { Text("Nueva") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FundChips(
    funds: List<Pair<Long, String>>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        funds.forEach { (id, name) ->
            FilterChip(
                selected = selectedId == id,
                onClick = { onSelect(id) },
                label = { Text(name) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SavingBlue.copy(alpha = 0.16f),
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun FromSavingsRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Savings,
            contentDescription = null,
            tint = SavingBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Sale de tus ahorros",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Descuenta el gasto de uno de tus fondos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun NumericKeypad(
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("000", "0", "⌫")
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (key) {
                                "⌫" -> onDelete()
                                else -> key.forEach(onDigit)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.aspectRatio(2.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (key == "⌫") {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Backspace,
                contentDescription = "Borrar",
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = key,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
