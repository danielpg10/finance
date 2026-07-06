package com.example.finance.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finance.di.AppViewModelProvider
import com.example.finance.domain.model.FundType
import com.example.finance.ui.components.MoneyTextField
import com.example.finance.util.toMoney

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.finished) {
        if (uiState.finished) onFinished()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            StepIndicator(currentStep = uiState.step, totalSteps = 3)
            Spacer(modifier = Modifier.height(40.dp))
            AnimatedContent(targetState = uiState.step, label = "onboardingStep") { step ->
                when (step) {
                    0 -> NameStep(
                        name = uiState.name,
                        canContinue = uiState.canContinueName,
                        onNameChange = viewModel::onNameChange,
                        onContinue = viewModel::nextStep
                    )
                    1 -> FundsStep(
                        funds = uiState.funds,
                        onAddFund = viewModel::addFund,
                        onRemoveFund = viewModel::removeFund,
                        onBack = viewModel::previousStep,
                        onContinue = viewModel::nextStep
                    )
                    else -> GoalStep(
                        goalName = uiState.goalName,
                        goalTarget = uiState.goalTarget,
                        canFinish = uiState.canContinueGoal,
                        onGoalNameChange = viewModel::onGoalNameChange,
                        onGoalTargetChange = viewModel::onGoalTargetChange,
                        onBack = viewModel::previousStep,
                        onFinish = viewModel::finish
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Column {
        Text(
            text = "Paso ${currentStep + 1} de $totalSteps",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(totalSteps) { index ->
                val color by animateColorAsState(
                    targetValue = if (index <= currentStep)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    label = "stepColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(color, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
private fun StepHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun NameStep(
    name: String,
    canContinue: Boolean,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column {
        Text(
            text = "Hola 👋",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vamos a construir tu meta de ahorro.\n¿Cómo quieres que te llame?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(28.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tu nombre") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))
        Button(
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Continuar", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun FundsStep(
    funds: List<DraftFund>,
    onAddFund: (String, FundType, Long) -> Unit,
    onRemoveFund: (Int) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var fundName by remember { mutableStateOf("") }
    var fundBalance by remember { mutableStateOf("") }
    var fundType by remember { mutableStateOf(FundType.BANK) }

    Column {
        StepHeader(
            icon = Icons.Outlined.AccountBalance,
            title = "Tus fondos",
            subtitle = "¿Dónde guardas tus ahorros? Agrega tus bancos, billeteras o efectivo con lo que ya tengas."
        )
        Spacer(modifier = Modifier.height(24.dp))
        funds.forEachIndexed { index, fund ->
            DraftFundCard(
                fund = fund,
                onRemove = { onRemoveFund(index) }
            )
        }
        if (funds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
        }
        OutlinedTextField(
            value = fundName,
            onValueChange = { fundName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre") },
            placeholder = { Text("Bancolombia, Nequi, Efectivo…") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        MoneyTextField(
            value = fundBalance,
            onValueChange = { fundBalance = it },
            label = "Cuánto tienes ahorrado ahí"
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FundType.entries.forEach { type ->
                FilterChip(
                    selected = fundType == type,
                    onClick = { fundType = type },
                    label = { Text(fundTypeLabel(type)) },
                    leadingIcon = {
                        Icon(
                            imageVector = fundTypeIcon(type),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                onAddFund(fundName.trim(), fundType, fundBalance.toLongOrNull() ?: 0L)
                fundName = ""
                fundBalance = ""
            },
            enabled = fundName.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Agregar fondo")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (fundName.isNotBlank()) {
                    onAddFund(fundName.trim(), fundType, fundBalance.toLongOrNull() ?: 0L)
                    fundName = ""
                    fundBalance = ""
                }
                onContinue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = if (funds.isEmpty() && fundName.isBlank()) "Omitir por ahora" else "Continuar",
                style = MaterialTheme.typography.titleMedium
            )
        }
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text("Atrás")
        }
    }
}

@Composable
private fun DraftFundCard(
    fund: DraftFund,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = fundTypeIcon(fund.type),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fund.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = fund.initialBalance.toMoney(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Quitar fondo",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun GoalStep(
    goalName: String,
    goalTarget: String,
    canFinish: Boolean,
    onGoalNameChange: (String) -> Unit,
    onGoalTargetChange: (String) -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    Column {
        StepHeader(
            icon = Icons.Outlined.Savings,
            title = "Tu meta",
            subtitle = "Define para qué estás ahorrando y cuánto necesitas. Podrás editarla cuando quieras."
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = goalName,
            onValueChange = onGoalNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre de la meta") },
            placeholder = { Text("Viaje, moto, estudios…") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        MoneyTextField(
            value = goalTarget,
            onValueChange = onGoalTargetChange,
            label = "Monto objetivo"
        )
        Spacer(modifier = Modifier.height(36.dp))
        Button(
            onClick = onFinish,
            enabled = canFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Empezar a ahorrar", style = MaterialTheme.typography.titleMedium)
        }
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text("Atrás")
        }
    }
}

private fun fundTypeIcon(type: FundType): ImageVector = when (type) {
    FundType.BANK -> Icons.Outlined.AccountBalance
    FundType.CASH -> Icons.Outlined.Payments
    FundType.DIGITAL_WALLET -> Icons.Outlined.PhoneAndroid
}

private fun fundTypeLabel(type: FundType): String = when (type) {
    FundType.BANK -> "Banco"
    FundType.CASH -> "Efectivo"
    FundType.DIGITAL_WALLET -> "Billetera"
}
