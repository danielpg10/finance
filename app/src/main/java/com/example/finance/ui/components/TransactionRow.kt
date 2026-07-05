package com.example.finance.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finance.domain.model.Transaction
import com.example.finance.domain.model.TransactionType
import com.example.finance.ui.theme.IncomeGreen
import com.example.finance.ui.theme.SavingBlue
import com.example.finance.util.DateFormatter
import com.example.finance.util.toMoney

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionRow(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null
) {
    val (icon, iconColor, title) = when (transaction.type) {
        TransactionType.INCOME -> Triple(Icons.AutoMirrored.Rounded.TrendingUp, IncomeGreen, transaction.note ?: "Ingreso")
        TransactionType.SAVING -> Triple(
            Icons.Rounded.Savings,
            SavingBlue,
            transaction.fundName?.let { "Aporte a $it" } ?: "Aporte a meta"
        )
        TransactionType.EXPENSE -> Triple(
            CategoryVisuals.icon(transaction.categoryIcon),
            transaction.categoryColor?.let { Color(it) } ?: MaterialTheme.colorScheme.secondary,
            transaction.categoryName ?: "Gasto"
        )
    }
    val amountText = when (transaction.type) {
        TransactionType.INCOME -> "+${transaction.amount.toMoney()}"
        else -> "-${transaction.amount.toMoney()}"
    }
    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> IncomeGreen
        TransactionType.SAVING -> SavingBlue
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongPress)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(iconColor.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            val subtitle = buildString {
                append(DateFormatter.formatDay(transaction.date))
                if (transaction.type == TransactionType.EXPENSE && !transaction.note.isNullOrBlank()) {
                    append(" · ${transaction.note}")
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = amountText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}
