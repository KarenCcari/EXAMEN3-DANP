package com.danp.lecturas_project.database


import androidx.room.Database
import androidx.room.RoomDatabase



@Database(
    entities = [LecturasEntity::class],
    exportSchema = false,
    version = 2
)

abstract class LecturasDataBase : RoomDatabase() {

    abstract fun lecturaDao(): LecturasDao
    //abstract fun rankingDao(): RankingDao
}