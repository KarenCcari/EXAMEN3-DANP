package com.danp.lecturas_project.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*


import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.danp.lecturas_project.R
import com.danp.lecturas_project.ui.theme.Purple200
import com.danp.lecturas_project.ui.theme.Purple500
import com.danp.lecturas_project.ui.theme.Purple700
import com.danp.lecturas_project.ui.theme.cyan


@Composable
fun Home(navController: NavHostController) {

    Column( modifier = Modifier
        .padding(12.dp)
        .fillMaxSize()) {
        Text(
            text = "Bienvenid@ !!!",

            style = TextStyle(color = Purple700, fontSize = 42.sp, fontWeight = FontWeight.Black , fontFamily = FontFamily.Serif)
        )

        Text(
            text = "Dirigirse a la seccion de perfil del navigation",

            style = TextStyle(color = cyan, fontSize = 12.sp, fontWeight = FontWeight.Black , fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
        )


    }



}
