package com.example.finance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryVisuals {

    private val icons: Map<String, ImageVector> = mapOf(
        "restaurant" to Icons.Rounded.Restaurant,
        "car" to Icons.Rounded.DirectionsCar,
        "party" to Icons.Rounded.Celebration,
        "health" to Icons.Rounded.Favorite,
        "home" to Icons.Rounded.Home,
        "shopping" to Icons.Rounded.ShoppingBag,
        "subscriptions" to Icons.Rounded.Subscriptions,
        "other" to Icons.Rounded.MoreHoriz
    )

    fun icon(key: String?): ImageVector = icons[key] ?: Icons.Rounded.MoreHoriz
}
