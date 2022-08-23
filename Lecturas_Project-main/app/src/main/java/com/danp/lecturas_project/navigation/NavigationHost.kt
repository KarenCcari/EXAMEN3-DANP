package com.danp.lecturas_project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.danp.lecturas_project.navigation.Destinations.*
import com.danp.lecturas_project.presentation.screens.*

@Composable
fun NavigationHost(
    navController: NavHostController,
    /*darkMode: MutableState<Boolean>*/
) {
    NavHost(navController = navController, startDestination = Home.route) {
        composable(Home.route) {
            Home(navController)
        }

        //composable(Lecturas.route) {
          //  Lecturas(navController)
        //}

        //composable(Ranking.route) {
        //}           Ranking(navController
         //   )
       // }

        composable(Perfil.route) {
            Perfil(navController)
        }
    }
}