package com.danp.lecturas_project

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.danp.lecturas_project.database.LecturasDataBase
import com.danp.lecturas_project.database.LecturasEntity
import com.danp.lecturas_project.datastore.Preferencias
import com.danp.lecturas_project.navigation.Destinations.*
import com.danp.lecturas_project.navigation.NavigationHost
import com.danp.lecturas_project.presentation.components.BottomNavigationBar
import com.danp.lecturas_project.presentation.components.TopFBar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    companion object {
        lateinit var db: LecturasDataBase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            db = Room.databaseBuilder(LocalContext.current, LecturasDataBase::class.java,
            "lecturas_l").build()
            compararData(db)
            MainScreen()
        }
    }


}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun compararData(db: LecturasDataBase) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    scope.launch {
        val cantidad = db.lecturaDao().getCantidad()
        FirebaseFirestore.getInstance().collection("puntaje").get()
            .addOnSuccessListener { puntajes ->
                val cantidad2 = puntajes.size()
                if (cantidad != cantidad2) {
                    scope.launch {
                        db.lecturaDao().deleteAll()
                    }
                    for (puntos in puntajes) {
                        scope.launch {
                            db.lecturaDao().insert(
                                LecturasEntity(
                                    puntos.data?.get("id").toString().toInt(),
                                    puntos.data?.get("titulo").toString(),
                                    puntos.data?.get("texto").toString(),
                                    puntos.data?.get("puntaje").toString().toInt(),
                                    puntos.data?.get("usuario").toString(),
                                    puntos.data?.get("orden").toString().toInt(),
                                    puntos.data?.get("id_lectura").toString()
                                )
                            )
                        }
                    }
                    Log.d("Mensaje", "Se terminó con los $cantidad2 registros")
                }
                else {
                    Log.d("Mensaje","Local: $cantidad , Nube: $cantidad2")
                }
            }
    }
}

@Composable
fun MainScreen() {

    val context = LocalContext.current
    val dataStore = Preferencias(context)
    val skipLogin = dataStore.getSkipSesion.collectAsState(initial = false).value
    val estadoSesion = dataStore.getEstadoSesion.collectAsState(initial = false).value
    val openLogin = remember { mutableStateOf(true) }
    if (openLogin.value){
        if (skipLogin || estadoSesion){
            openLogin.value = !openLogin.value
        }
        else {
            LoginScreen()
        }
    }
    if (!openLogin.value) {
        BottomNavigation(openLogin)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BottomNavigation(openLogin: MutableState<Boolean>) {
    val navController= rememberNavController()
    val navigationItem = listOf(
        Home,
        //Lecturas,
        //Ranking,
        Perfil
    )

    Scaffold(
        topBar = { TopFBar(openLogin)},
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = navigationItem)
        }
    ){
        NavigationHost(navController = navController)
    }
}