package com.danp.lecturas_project.presentation.components

import android.util.Log
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.danp.lecturas_project.datastore.Preferencias
import com.danp.lecturas_project.navigation.Destinations
import com.danp.lecturas_project.ui.theme.Purple700
import com.danp.lecturas_project.ui.theme.Purple500
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun TopFBar(
    openLogin: MutableState<Boolean>
){
    val mContext = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = Preferencias(mContext)
    val estadoSesion = dataStore.getEstadoSesion.collectAsState(initial = false).value
    val skipSesion = dataStore.getSkipSesion.collectAsState(initial = false).value

    TopAppBar(
        title = { Text(
            text = "Improve  your  Reading",
            fontSize = 20.sp,

            ) },
        actions = {
            if (skipSesion) {
                IconButton(onClick = {
                    scope.launch {
                        dataStore.saveSkipSesion(false)
                        openLogin.value = !openLogin.value
                    }
                }
                ){
                    Icon(Icons.Default.Person, "Perfil")
                }
            }
            if (estadoSesion){
                IconButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    scope.launch {
                        dataStore.saveEstadoSesion(false)
                        dataStore.saveEmail("")
                        dataStore.saveNombre("")
                        openLogin.value = !openLogin.value
                    }
                }
                ){
                    Icon(Icons.Default.Logout, "Salir")
                }
            }
        },
        backgroundColor = Purple700,
        contentColor = Color.White
    )
}