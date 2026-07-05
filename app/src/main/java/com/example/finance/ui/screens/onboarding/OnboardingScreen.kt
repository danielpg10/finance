package com.example.finance.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finance.di.AppViewModelProvider
import com.example.finance.domain.model.FundType
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
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { (uiState.step + 1) / 3f },
                modifier = Modifier.fillMaxWidth()
            )
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
private fun NameStep(
    name: String,
    canContinue: Boolean,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column {
        Text(
            text = "Hola 👋",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¿Cómo quieres que te llame?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tu nombre") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Continuar")
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
        Text(
            text = "Tus fondos",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¿Dónde guardas tus ahorros? Agrega tus bancos, billeteras o efectivo, con lo que ya tengas ahorrado.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        funds.forEachIndexed { index, fund ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(fundTypeIcon(fund.type), contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(fund.name, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = fund.initialBalance.toMoney(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onRemoveFund(index) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Eliminar fondo")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = fundName,
            onValueChange = { fundName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre (ej. Bancolombia, Nequi, Efectivo)") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = fundBalance,
            onValueChange = { value -> fundBalance = value.filter(Char::isDigit).take(12) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Cuánto tienes ahorrado ahí") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FundType.entries.forEach { type ->
                FilterChip(
                    selected = fundType == type,
                    onClick = { fundType = type },
                    label = { Text(fundTypeLabel(type)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                onAddFund(fundName.trim(), fundType, fundBalance.toLongOrNull() ?: 0L)
                fundName = ""
                fundBalance = ""
            },
            enabled = fundName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Agregar fondo")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            TextButton(onClick = onBack) { Text("Atrás") }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onContinue,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (funds.isEmpty()) "Omitir" else "Continuar")
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
        Icon(
            imageVector = Icons.Rounded.Savings,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu meta",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Define para qué estás ahorrando y cuánto necesitas.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = goalName,
            onValueChange = onGoalNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre de la meta") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = goalTarget,
            onValueChange = onGoalTargetChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Monto objetivo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            prefix = { Text("$") },
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            TextButton(onClick = onBack) { Text("Atrás") }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onFinish,
                enabled = canFinish,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Empezar")
            }
        }
    }
}

private fun fundTypeIcon(type: FundType): ImageVector = when (type) {
    FundType.BANK -> Icons.Rounded.AccountBalance
    FundType.CASH -> Icons.Rounded.Payments
    FundType.DIGITAL_WALLET -> Icons.Rounded.PhoneAndroid
}

internal fun fundTypeLabel(type: FundType): String = when (type) {
    FundType.BANK -> "Banco"
    FundType.CASH -> "Efectivo"
    FundType.DIGITAL_WALLET -> "Billetera"
}
