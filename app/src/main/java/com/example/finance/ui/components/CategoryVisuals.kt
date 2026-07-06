package com.example.finance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryVisuals {

    val catalog: List<Pair<String, ImageVector>> = listOf(
        "restaurant" to Icons.Outlined.Restaurant,
        "car" to Icons.Outlined.DirectionsCar,
        "party" to Icons.Outlined.Celebration,
        "health" to Icons.Outlined.Favorite,
        "home" to Icons.Outlined.Home,
        "shopping" to Icons.Outlined.ShoppingBag,
        "subscriptions" to Icons.Outlined.Subscriptions,
        "school" to Icons.Outlined.School,
        "book" to Icons.AutoMirrored.Outlined.MenuBook,
        "work" to Icons.Outlined.Work,
        "pets" to Icons.Outlined.Pets,
        "flight" to Icons.Outlined.Flight,
        "fitness" to Icons.Outlined.FitnessCenter,
        "coffee" to Icons.Outlined.LocalCafe,
        "gift" to Icons.Outlined.CardGiftcard,
        "clothes" to Icons.Outlined.Checkroom,
        "phone" to Icons.Outlined.Smartphone,
        "wifi" to Icons.Outlined.Wifi,
        "games" to Icons.Outlined.SportsEsports,
        "music" to Icons.Outlined.MusicNote,
        "movies" to Icons.Outlined.Movie,
        "beauty" to Icons.Outlined.Spa,
        "other" to Icons.Outlined.MoreHoriz
    )

    val colorPalette: List<Long> = listOf(
        0xFFF97316,
        0xFFF59E0B,
        0xFF10B981,
        0xFF14B8A6,
        0xFF3B82F6,
        0xFF6366F1,
        0xFF8B5CF6,
        0xFFEC4899,
        0xFFEF4444,
        0xFF64748B
    )

    private val icons: Map<String, ImageVector> = catalog.toMap()

    fun icon(key: String?): ImageVector = icons[key] ?: Icons.Outlined.MoreHoriz
}
