package com.example.finance.ui.screens.transaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finance.di.AppViewModelProvider
import com.example.finance.domain.model.Category
import com.example.finance.domain.model.TransactionType
import com.example.finance.ui.components.CategoryVisuals
import com.example.finance.ui.theme.ExpenseRed
import com.example.finance.ui.theme.IncomeGreen
import com.example.finance.ui.theme.SavingBlue
import com.example.finance.util.toMoney

private val TransactionType.accent: Color
    get() = when (this) {
        TransactionType.EXPENSE -> ExpenseRed
        TransactionType.INCOME -> IncomeGreen
        TransactionType.SAVING -> SavingBlue
    }

private val TransactionType.label: String
    get() = when (this) {
        TransactionType.EXPENSE -> "Gasto"
        TransactionType.INCOME -> "Ingreso"
        TransactionType.SAVING -> "Aporte"
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onClose: () -> Unit,
    viewModel: AddTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val funds by viewModel.funds.collectAsStateWithLifecycle()
    var step by rememberSaveable { mutableIntStateOf(0) }
    var showCreateCategory by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onClose()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (step == 0) "Nuevo movimiento" else "Detalles",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (step == 0) onClose() else step = 0 }) {
                        Icon(
                            imageVector = if (step == 0)
                                Icons.Outlined.Close
                            else
                                Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = if (step == 0) "Cerrar" else "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = step,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally(tween(280)) { it } + fadeIn())
                        .togetherWith(slideOutHorizontally(tween(280)) { -it / 3 } + fadeOut())
                } else {
                    (slideInHorizontally(tween(280)) { -it / 3 } + fadeIn())
                        .togetherWith(slideOutHorizontally(tween(280)) { it } + fadeOut())
                }
            },
            label = "addTransactionStep"
        ) { currentStep ->
            if (currentStep == 0) {
                AmountStep(
                    uiState = uiState,
                    onTypeChange = viewModel::onTypeChange,
                    onDigit = viewModel::onDigit,
                    onDelete = viewModel::onDeleteDigit,
                    onContinue = { step = 1 }
                )
            } else {
                DetailsStep(
                    uiState = uiState,
                    categories = categories,
                    funds = funds.map { it.id to it.name },
                    onEditAmount = { step = 0 },
                    onCategorySelected = viewModel::onCategorySelected,
                    onFundSelected = viewModel::onFundSelected,
                    onFromSavingsChange = viewModel::onFromSavingsChange,
                    onNoteChange = viewModel::onNoteChange,
                    onCreateCategory = { showCreateCategory = true },
                    onSave = viewModel::save
                )
            }
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
private fun AmountStep(
    uiState: AddTransactionUiState,
    onTypeChange: (TransactionType) -> Unit,
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        TypePills(selected = uiState.type, onSelect = onTypeChange)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (uiState.type) {
                    TransactionType.EXPENSE -> "¿Cuánto gastaste?"
                    TransactionType.INCOME -> "¿Cuánto recibiste?"
                    TransactionType.SAVING -> "¿Cuánto vas a aportar?"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            val amountColor by animateColorAsState(
                targetValue = if (uiState.amountValue > 0)
                    uiState.type.accent
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                label = "amountColor"
            )
            Text(
                text = if (uiState.amountValue > 0) uiState.amountValue.toMoney() else "$0",
                fontSize = 58.sp,
                lineHeight = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.5).sp,
                color = amountColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }

        NumericKeypad(onDigit = onDigit, onDelete = onDelete)

        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onContinue,
            enabled = uiState.amountValue > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TypePills(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            TransactionType.EXPENSE,
            TransactionType.INCOME,
            TransactionType.SAVING
        ).forEach { type ->
            val isSelected = selected == type
            val container by animateColorAsState(
                targetValue = if (isSelected)
                    type.accent.copy(alpha = 0.14f)
                else
                    MaterialTheme.colorScheme.surface,
                label = "pillContainer"
            )
            val content by animateColorAsState(
                targetValue = if (isSelected)
                    type.accent
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                label = "pillContent"
            )
            Surface(
                onClick = { onSelect(type) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = container,
                shadowElevation = if (isSelected) 0.dp else 1.dp
            ) {
                Text(
                    text = type.label,
                    modifier = Modifier.padding(vertical = 13.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = content
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailsStep(
    uiState: AddTransactionUiState,
    categories: List<Category>,
    funds: List<Pair<Long, String>>,
    onEditAmount: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onFundSelected: (Long) -> Unit,
    onFromSavingsChange: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onCreateCategory: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        AmountSummaryCard(
            type = uiState.type,
            amount = uiState.amountValue,
            onClick = onEditAmount
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(22.dp))
            when (uiState.type) {
                TransactionType.EXPENSE -> {
                    SectionTitle("¿En qué lo gastaste?")
                    CategoryGrid(
                        categories = categories,
                        selectedId = uiState.selectedCategoryId,
                        onSelect = onCategorySelected,
                        onCreateNew = onCreateCategory
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    FromSavingsCard(
                        checked = uiState.fromSavings,
                        onCheckedChange = onFromSavingsChange
                    )
                    AnimatedVisibility(visible = uiState.fromSavings) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            FundChips(
                                funds = funds,
                                selectedId = uiState.selectedFundId,
                                onSelect = onFundSelected
                            )
                        }
                    }
                }
                TransactionType.SAVING -> {
                    SectionTitle("¿A qué fondo va tu aporte?")
                    FundChips(
                        funds = funds,
                        selectedId = uiState.selectedFundId,
                        onSelect = onFundSelected
                    )
                }
                TransactionType.INCOME -> {
                    SectionTitle("Detalle del ingreso")
                    Text(
                        text = "Registra de dónde vino en la nota para reconocerlo después.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Nota")
            TextField(
                value = uiState.note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = when (uiState.type) {
                            TransactionType.EXPENSE -> "Opcional — ej. Pago semestre"
                            TransactionType.INCOME -> "Opcional — ej. Salario, venta"
                            TransactionType.SAVING -> "Opcional — ej. Ahorro del mes"
                        }
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = onSave,
            enabled = uiState.canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Guardar movimiento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AmountSummaryCard(
    type: TransactionType,
    amount: Long,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = type.accent.copy(alpha = 0.10f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = amount.toMoney(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = type.accent
                )
            }
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Editar monto",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryGrid(
    categories: List<Category>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onCreateNew: () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 4
    ) {
        categories.forEach { category ->
            CategoryTile(
                name = category.name,
                icon = CategoryVisuals.icon(category.icon),
                color = Color(category.color),
                selected = selectedId == category.id,
                onClick = { onSelect(category.id) },
                modifier = Modifier.weight(1f)
            )
        }
        CategoryTile(
            name = "Nueva",
            icon = Icons.Outlined.Add,
            color = MaterialTheme.colorScheme.secondary,
            selected = false,
            onClick = onCreateNew,
            modifier = Modifier.weight(1f)
        )
        val remainder = (categories.size + 1) % 4
        if (remainder != 0) {
            repeat(4 - remainder) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CategoryTile(
    name: String,
    icon: ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bubbleColor by animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = 0.12f),
        label = "categoryBubble"
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) Color.White else color,
        label = "categoryIcon"
    )
    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = if (selected) 8.dp else 0.dp,
                    shape = CircleShape,
                    spotColor = color.copy(alpha = 0.55f)
                )
                .background(bubbleColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = iconColor,
                modifier = Modifier.size(25.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
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
            ElevatedFilterChip(
                selected = selectedId == id,
                onClick = { onSelect(id) },
                label = { Text(name) },
                shape = RoundedCornerShape(14.dp),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedContainerColor = SavingBlue.copy(alpha = 0.16f),
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun FromSavingsCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Savings,
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
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (key == "⌫") {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = "Borrar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = key,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
