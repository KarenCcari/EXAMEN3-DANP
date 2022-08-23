package com.danp.lecturas_project.presentation.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button


import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.danp.lecturas_project.datastore.Preferencias
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun Perfil(navController: NavHostController) {
    var imagenBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    val mStorage = FirebaseStorage.getInstance().getReference()
    //Datastore
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = Preferencias(context)
    val email = dataStore.getEmail.collectAsState(initial = "").value



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            //.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        }

        Button(onClick = {
            val toConvert = imagenBitmap
            val bytes: ByteArrayOutputStream = ByteArrayOutputStream()
            toConvert?.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                toConvert,
                "title",
                null
            )
            val uriImage = Uri.parse(path)
            val filePath = uriImage.lastPathSegment?.let {
                mStorage.child("fotos")
                    .child(it)
            }
            filePath?.putFile(uriImage)?.addOnSuccessListener {
                Toast.makeText(context, "Imagen almacenada con éxito.", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                Toast.makeText(context, "Imagen rechazada.", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Guardar")
        }
        TomarImagen(onImageCapture = { imagenBitmap = it })



}

@Composable
fun TomarImagen(onImageCapture: (Bitmap?) -> Unit) {

    val context = LocalContext.current
    var bitmapDos by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val camaraOn = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { btm: Bitmap? ->
        bitmapDos = btm
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (camaraOn.value) {
                cameraLauncher.launch()
            }
            Toast.makeText(context, "Permiso concedido!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso denegado!", Toast.LENGTH_SHORT).show()
        }
    }
    Row() {
        Button(onClick = {
            camaraOn.value = true
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) -> {
                    cameraLauncher.launch()
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }) {
            Text(text = "Foto Baja Calidad")
        }
    }

    if (camaraOn.value) {
        bitmapDos?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(900.dp)
            )
            onImageCapture(btm)
        }
    }

}