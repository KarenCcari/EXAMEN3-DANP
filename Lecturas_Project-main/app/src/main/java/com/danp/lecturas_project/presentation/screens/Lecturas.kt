package com.danp.lecturas_project.presentation.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*


import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.Room
import com.danp.lecturas_project.MainActivity
import com.danp.lecturas_project.database.LecturasDataBase
import com.danp.lecturas_project.database.LecturasEntity
import com.danp.lecturas_project.datastore.Preferencias
import com.danp.lecturas_project.showAlert
import com.danp.lecturas_project.ui.theme.Purple500
import com.danp.lecturas_project.ui.theme.Purple700
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun Lecturas(navController: NavHostController) {
    val eleccion = remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (eleccion.value) {
            botones(eleccion, navController)
        }
        else {
            OpenLectura(eleccion = eleccion, navController = navController)
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun botones(eleccion: MutableState<Boolean>, navController: NavHostController) {
    val dbf = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = Preferencias(context)
    val usuario = dataStore.getNombre.collectAsState(initial = "").value
    val titulo = remember { mutableStateOf("") }
    val texto = remember { mutableStateOf("") }


    val dbl = Room.databaseBuilder(
        LocalContext.current,
        LecturasDataBase::class.java, "lectura"
    ).build()

    Text(
        text = "Tu última lectura.",
        style = TextStyle(color = Color.Black, fontSize = 42.sp, fontWeight = FontWeight.Black)
    )

    scope.launch {
        val lectura = dbl.lecturaDao().getUltimo(user = usuario)
        lectura?.let {
            titulo.value = it.titulo
            texto.value = it.texto
        }
    }

    Text(
        text = titulo.value,
        style = TextStyle(color = Color.Black, fontSize = 42.sp, fontWeight = FontWeight.Black)
    )
    Text(
        text = texto.value,
        style = TextStyle(color = Color.Black, fontSize = 21.sp),
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(end = 15.dp)
    )

    Button(onClick = {
        eleccion.value = !eleccion.value
    },
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        border = BorderStroke(4.dp, Purple700),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black, backgroundColor = Purple500)
    ) {
        Text(text = "Iniciar una nueva lectura al azar.", Modifier.padding(start = 10.dp))
    }


}

@Composable
fun OpenLectura(eleccion: MutableState<Boolean>, navController: NavHostController) {


    //Datastore
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = Preferencias(context)
    val usuario = dataStore.getNombre.collectAsState(initial = "").value

    //Firebase
    val dbf = FirebaseFirestore.getInstance()
    val titulo = remember { mutableStateOf("") }
    val texto = remember { mutableStateOf("") }
    val lectura_id = remember { mutableStateOf("") }
    val cargarPreguntas = remember { mutableStateOf(false) }
    var preguntas = remember { mutableListOf<Pregunta>() }
    var puntaje by rememberSaveable{ mutableStateOf(arrayOf(0,0,0,0,0,0,0,0,0,0)) }

    val dbl = Room.databaseBuilder(
        LocalContext.current,
        LecturasDataBase::class.java, "lectura"
    ).build()


    dbf.collection("puntaje").whereEqualTo("usuario", usuario).get()
        .addOnSuccessListener{ documents ->
            //var lect: List<DocumentSnapshot>
            dbf.collection("lecturas").get()
                .addOnSuccessListener { lecturas ->
                    var restantes = lecturas.documents
                    for (document in documents) {
                        for (lectura in lecturas) {
                            if (document.data["id_lectura"].toString() == lectura.id) {
                                restantes.remove(lectura)

                            }
                        }
                    }
                    val cantidad = restantes.size
                    if (cantidad >= 1) {
                        val resultado = restantes[0]
                        titulo.value = resultado.data?.get("titulo").toString()
                        texto.value = resultado.data?.get("texto").toString()
                        lectura_id.value = resultado.id
                    }
                }
            dbf.collection("preguntas").whereEqualTo("id_lectura", lectura_id.value).get()
                .addOnSuccessListener { preguntasDB ->
                    var cont = 0;
                    for (preg in preguntasDB){
                        preguntas.add(cont, Pregunta(
                            preg.data?.get("pregunta").toString(),
                            preg.data?.get("alternativaA").toString(),
                            preg.data?.get("alternativaB").toString(),
                            preg.data?.get("alternativaC").toString(),
                            preg.data?.get("respuesta").toString(),
                            preg.data?.get("numero").toString().toInt()
                        ))
                        cont++
                    }
                }
        }

    Text(
        text = titulo.value,
        style = TextStyle(color = Color.Black, fontSize = 42.sp, fontWeight = FontWeight.Black)
    )
    Text(
        text = texto.value,
        style = TextStyle(color = Color.Black, fontSize = 21.sp),
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(end = 15.dp)
    )

    if (!cargarPreguntas.value) {
        Button(onClick = {
            cargarPreguntas.value = !cargarPreguntas.value
        }) {
            Text(text = "Cargar Preguntas")
        }
    }

    if (cargarPreguntas.value) {
        Log.d("Pregunta dis", preguntas[0].alternativaC)
        for (pregunta in preguntas) {
            //Log.d("Pregunta", pregunta.data?.get("alternativaA").toString())
            definir(pregunta, esCorrecto = { puntaje[pregunta.numero - 1] = it })

        }
        Button(onClick = {
            var nota = 0
            for(punto in puntaje) {
                nota += punto
            }
            //Log.d("Otros", "Nota: ${nota}")

            FirebaseFirestore.getInstance().collection("puntaje").get()
                .addOnSuccessListener { puntajes ->
                    var cantidad2 = puntajes.size()
                scope.launch {
                    dbf.collection("puntaje").document().set(
                        hashMapOf(
                            "id" to (cantidad2+1),
                            "titulo" to titulo.value,
                            "texto" to texto.value,
                            "id_lectura" to lectura_id.value,
                            "puntaje" to nota,
                            "usuario" to usuario,
                            "orden" to dbl.lecturaDao().getOrden(usuario)+1
                        )
                    )
                }
                scope.launch {
                    dbl.lecturaDao().insert(
                        LecturasEntity(id = (cantidad2+1), titulo = titulo.value,
                            texto = texto.value, puntaje = nota, usuario = usuario,
                            orden = dbl.lecturaDao().getOrden(usuario)+1, id_lectura = lectura_id.value)
                    )
                }


            }
            navController.navigate("Ranking")



        }) {
            Text(text = "Calificar")
        }
    }
    //Log.d("Otros", puntaje[0].toString())

}

@Composable
fun definir(pregunta: Pregunta, esCorrecto: (Int) -> Unit) {
    val respuesta = pregunta.respuesta
    val opciones = listOf(
        pregunta.alternativaA,
        pregunta.alternativaB,
        pregunta.alternativaC
    )
    val currentSelection = remember { mutableStateOf(opciones.first()) }
    Text(
        text = pregunta.pregunta,
        style = TextStyle(color = Color.Black, fontSize = 21.sp, fontWeight = FontWeight.Black)
    )
    RadioGroup(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        items = opciones,
        selection = currentSelection.value,
        onItemClick = { clickedItem ->
            currentSelection.value = clickedItem
            Log.d("Otro", currentSelection.value)
        }
    )
    if(respuesta.equals(currentSelection.value)) {
        esCorrecto(1)
        Log.d("Otros", "correcto")
    }
    else{
        esCorrecto(0)
        Log.d("Otros", "incorrecto")
    }

}

@Composable
fun RadioGroup(
    modifier: Modifier,
    items: List<String>,
    selection: String,
    onItemClick: ((String) -> Unit)
) {
    Column(modifier = modifier) {
        items.forEach { item ->
            LabelledRadioButton(
                modifier = Modifier.fillMaxWidth(),
                label = item,
                selected = item == selection,
                onClick = {
                    onItemClick(item)
                }
            )
        }
    }
}

@Composable
fun LabelledRadioButton(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors()
) {

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = colors
        )

        Text(
            text = label,
            style = MaterialTheme.typography.body1.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

data class Pregunta(
    val pregunta: String,
    val alternativaA: String,
    val alternativaB: String,
    val alternativaC: String,
    val respuesta: String,
    val numero: Int)