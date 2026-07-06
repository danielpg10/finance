package com.example.finance.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.finance.ui.navigation.BottomNavItem
import com.example.finance.ui.theme.EmeraldDark
import com.example.finance.ui.theme.EmeraldLight

@Composable
fun FloatingNavBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()
    val barShape = RoundedCornerShape(36.dp)
    val glassColor = if (darkTheme)
        Color(0xFF1C1E22).copy(alpha = 0.94f)
    else
        Color(0xFF15171A).copy(alpha = 0.94f)
    val glassBorder = Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.02f))
    )

    Row(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(74.dp)
            .shadow(
                elevation = 22.dp,
                shape = barShape,
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(barShape)
            .background(glassColor)
            .border(width = 1.dp, brush = glassBorder, shape = barShape),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.take(2).forEach { item ->
            NavItem(
                item = item,
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
        AddButton(onClick = onAdd)
        items.drop(2).forEach { item ->
            NavItem(
                item = item,
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

@Composable
private fun NavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF8B929B),
        label = "navItemColor"
    )
    val bubbleColor by animateColorAsState(
        targetValue = if (selected) Color.White.copy(alpha = 0.14f) else Color.Transparent,
        label = "navItemBubble"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .background(bubbleColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
private fun AddButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .shadow(elevation = 8.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(listOf(EmeraldLight, EmeraldDark))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Agregar movimiento",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
