package com.example.finance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryVisuals {

    val catalog: List<Pair<String, ImageVector>> = listOf(
        "restaurant" to Icons.Rounded.Restaurant,
        "car" to Icons.Rounded.DirectionsCar,
        "party" to Icons.Rounded.Celebration,
        "health" to Icons.Rounded.Favorite,
        "home" to Icons.Rounded.Home,
        "shopping" to Icons.Rounded.ShoppingBag,
        "subscriptions" to Icons.Rounded.Subscriptions,
        "school" to Icons.Rounded.School,
        "book" to Icons.AutoMirrored.Rounded.MenuBook,
        "work" to Icons.Rounded.Work,
        "pets" to Icons.Rounded.Pets,
        "flight" to Icons.Rounded.Flight,
        "fitness" to Icons.Rounded.FitnessCenter,
        "coffee" to Icons.Rounded.LocalCafe,
        "gift" to Icons.Rounded.CardGiftcard,
        "clothes" to Icons.Rounded.Checkroom,
        "phone" to Icons.Rounded.Smartphone,
        "wifi" to Icons.Rounded.Wifi,
        "games" to Icons.Rounded.SportsEsports,
        "music" to Icons.Rounded.MusicNote,
        "movies" to Icons.Rounded.Movie,
        "beauty" to Icons.Rounded.Spa,
        "other" to Icons.Rounded.MoreHoriz
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

    fun icon(key: String?): ImageVector = icons[key] ?: Icons.Rounded.MoreHoriz
}
