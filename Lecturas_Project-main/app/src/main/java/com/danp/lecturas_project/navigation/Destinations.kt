package com.danp.lecturas_project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinations(
    val route: String, //id destinos
    val title: String,
    val icon: ImageVector
) {
    object Home: Destinations("Home", "Principal", Icons.Filled.Home)
    ///object Lecturas: Destinations("Lecturas", "Lectura", Icons.Filled.Edit)
    //object Ranking: Destinations("Ranking", "Ranking", Icons.Filled.List)
    object Perfil: Destinations("Perfil", "Mi Perfil", Icons.Filled.AccountBox)
}
