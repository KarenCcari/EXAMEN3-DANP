package com.danp.lecturas_project.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LecturasEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "texto") val texto: String,
    @ColumnInfo(name = "puntaje") val puntaje: Int,
    @ColumnInfo(name = "usuario") val usuario: String,
    @ColumnInfo(name = "orden") val orden: Int,
    @ColumnInfo(name = "id_lectura") val id_lectura: String
)