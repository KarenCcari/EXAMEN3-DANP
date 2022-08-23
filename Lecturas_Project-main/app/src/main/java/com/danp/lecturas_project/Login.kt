package com.danp.lecturas_project

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danp.lecturas_project.datastore.Preferencias
import com.danp.lecturas_project.ui.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.coroutines.coroutineContext


@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun LoginScreen(){


    val tabItems = listOf("Iniciar Sesión", "Crear una cuenta")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()


    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Purple500,
            modifier = Modifier
                .padding(5.dp)
                .background(Color.Transparent)
                .clip(RoundedCornerShape(30.dp)),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .pagerTabIndicatorOffset(
                            pagerState, tabPositions
                        )
                        .width(0.dp)
                        .height(0.dp)
                )
            }
        ) {
            tabItems.forEachIndexed { index, title ->
                val color = remember { Animatable(Purple700) }
                LaunchedEffect(pagerState.currentPage == index) {
                    color.animateTo(
                        if (pagerState.currentPage == index)
                            Color.White else Purple500
                    )
                }

                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            style = if (pagerState.currentPage == index) {
                                TextStyle(
                                    color = Purple700,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black
                                )
                            } else {
                                TextStyle(
                                    color = Color.White,
                                    fontSize = 15.sp,
                                )
                            }
                        )
                    },
                    modifier = Modifier.background(
                        color = color.value,
                        shape = RoundedCornerShape(30.dp)
                    )
                )
            }
        }

        HorizontalPager(
            count = tabItems.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Purple200)
        ) { page ->
            if (page == 0){
                Column (
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    LoginForm()
                }

            }
            else{
                Column (
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    SignUpForm()
                }
            }

        }
    }
}

@Composable
fun SignUpForm() {

    val checked = remember { mutableStateOf(true) }
    var nombres by rememberSaveable{mutableStateOf<String?>(null)}
    var apellidos by rememberSaveable{mutableStateOf<String?>(null)}
    var email by rememberSaveable{mutableStateOf<String?>(null)}
    var username by rememberSaveable{mutableStateOf<String?>(null)}
    var password by rememberSaveable{mutableStateOf<String?>(null)}

    val auth = Firebase.auth
    //DataStore
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = Preferencias(context)


    TextFieldWithIcons(
        name = "Nombres",
        placeholder = "Ingrese sus nombres",
        icon = Icons.Default.Create,
        keyboardType = KeyboardType.Text,
        getText = {nombres = it}
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextFieldWithIcons(
        name = "Apellidos",
        placeholder = "Ingrese sus apellidos",
        icon = Icons.Default.Create,
        keyboardType = KeyboardType.Text,
        getText = {apellidos = it}
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextFieldWithIcons(
        name = "Nombre de usuario",
        placeholder = "Ingrese un nombre de usuario",
        icon = Icons.Default.AccountBox,
        keyboardType = KeyboardType.Text,
        getText = {username = it}
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextFieldWithIcons(
        name = "Correo Electrónico",
        placeholder = "Ingrese su Correo Electrónico",
        icon = Icons.Default.Email,
        keyboardType = KeyboardType.Email,
        getText = {email = it}
    )
    Spacer(modifier = Modifier.height(8.dp))
    PasswordTextField(getPassword = {password = it})
    LabelledCheckbox(
        checked = checked.value,
        onCheckedChange = { checked.value = it },
        label = "Aceptar términos y condiciones"
    )
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = {
        if (nombres.isNullOrEmpty() || apellidos.isNullOrEmpty() ||
         username.isNullOrEmpty() || email.isNullOrEmpty() ||
         password.isNullOrEmpty() ) {
            Log.d("Vacío", "Algún dato está  vacío.")
        }
        else{
            auth.createUserWithEmailAndPassword(email?:"", password?:"")
                .addOnCompleteListener{
                    if (it.isSuccessful) {
                        scope.launch {
                            dataStore.saveEstadoSesion(true)
                            dataStore.saveNombre(username?:"")
                            dataStore.saveEmail(email?:"")
                        }
                        FirebaseFirestore.getInstance().collection("usuarios").document(email?:"").set(
                            hashMapOf(
                                "nombre" to nombres,
                                "apellidos" to apellidos,
                                "email" to email,
                                "username" to username
                            )
                        )
                        context.startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        showAlert(context)
                    }
                }
        }
    },
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        border = BorderStroke(4.dp, Purple700),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black, backgroundColor = Purple500)
    ) {
        Image(
            painterResource(id = R.drawable.ic_signup),
            contentDescription = "Registrarse",
            modifier = Modifier.size(20.dp)
        )
        Text(text = "Registrarse", Modifier.padding(start = 10.dp))
    }
}

fun showAlert(context: Context) {

    Toast.makeText(context, "Se ha producido un error autenticando...", Toast.LENGTH_SHORT).show()

}

@Composable
fun LoginForm() {
    //Form
    var email by rememberSaveable{mutableStateOf<String?>(null)}
    var password by rememberSaveable{mutableStateOf<String?>(null)}

    val auth = Firebase.auth
    //DataStore
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = Preferencias(context)

    TextFieldWithIcons(
        name = "Correo Electrónico",
        placeholder = "Ingrese su Correo Electrónico",
        icon = Icons.Default.Email,
        keyboardType = KeyboardType.Email,
        getText = {email = it}
    )
    Spacer(modifier = Modifier.height(8.dp))
    PasswordTextField(getPassword = {password = it})
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = {
        if (email.isNullOrEmpty() || password.isNullOrEmpty() ) {

        }
        else {
            auth.signInWithEmailAndPassword(email ?: "", password ?: "")
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        scope.launch {
                            dataStore.saveEstadoSesion(true)
                            dataStore.saveNombre(email ?: "")
                            dataStore.saveEmail(email ?: "")
                        }
                        context.startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        showAlert(context)
                    }
                }
        }
    },
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        border = BorderStroke(4.dp, Purple700),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black, backgroundColor = Purple500)
    ) {
        Image(
            painterResource(id = R.drawable.ic_login),
            contentDescription = "Inicio de sesión",
            modifier = Modifier.size(20.dp)
        )
        Text(text = "Iniciar Sesión", Modifier.padding(start = 10.dp))
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = {
        //your onclick code here
    },
        elevation =  ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        border = BorderStroke(4.dp, Purple700),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black, backgroundColor = Purple500)
    ) {
        Image(
            painterResource(id = R.drawable.ic_google),
            contentDescription = "Inicio con Google",
            modifier = Modifier.size(20.dp)
        )
        Text(text = "Acceder con cuenta de Google", Modifier.padding(start = 10.dp))
    }
    Text(
        text = "... o puede ingresar sin usuario.",
        modifier = Modifier
            .padding(vertical = 5.dp)
            .clickable {
                scope.launch {
                    dataStore.saveSkipSesion(true)
                }
                context.startActivity(Intent(context, MainActivity::class.java))
            },
        fontSize = 12.sp,
        color = Purple700,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun TextFieldWithIcons(
    name: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType,
    getText:(String?) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(""))}
    OutlinedTextField(
        value = text,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = { Icon(imageVector = icon, contentDescription = "icono de campo") },
        //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
        onValueChange = {
            text = it
        },
        label = { Text(text = name) },
        placeholder = { Text(text = placeholder) }
    )
    getText(text.text)
}

@Composable
fun PasswordTextField(getPassword: (String?) -> Unit) {
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val showPassword = remember { mutableStateOf(false) }
    getPassword(text.text)
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
        },
        leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = "Contraseña")},
        label = { Text(text = "Contraseña")},
        placeholder = { Text(text = "Ingrese su contraseña")},
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = true,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        singleLine = true,
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val (icon, iconColor) = if (showPassword.value) {
                Pair(
                    Icons.Filled.Visibility,
                    colorResource(id = R.color.purple_500)
                )
            } else {
                Pair(
                    Icons.Filled.VisibilityOff,
                    colorResource(id = R.color.purple_700)
                )
            }
            IconButton(onClick = { showPassword.value = !showPassword.value }) {
                Icon(
                    icon,
                    contentDescription = "Visibilidad",
                    tint = iconColor
                )
            }
        }
    )
}


@Composable
fun LabelledCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    Row(
        modifier = modifier.height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = colors
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .clickable {
                    /* */
                },
            color = Purple700,
            textDecoration = TextDecoration.Underline
        )
    }
}