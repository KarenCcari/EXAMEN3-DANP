package com.danp.lecturas_project.pager


import com.danp.lecturas_project.MainActivity.Companion.db
import com.danp.lecturas_project.database.LecturasEntity
import kotlinx.coroutines.delay

class Repository  {
    lateinit var remoteDataSource: List<LecturasEntity>

    suspend fun getItems(page: Int, pageSize: Int): Result<List<LecturasEntity>> {
        remoteDataSource = db.lecturaDao().getPuntajes()
        delay(2000L)
        val startingIndex = page * pageSize
        return if(startingIndex + pageSize <= remoteDataSource.size) {
            Result.success(
                remoteDataSource.slice(startingIndex until startingIndex + pageSize)
            )
        } else Result.success(emptyList())
    }
}