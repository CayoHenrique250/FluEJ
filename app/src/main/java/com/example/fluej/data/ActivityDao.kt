package com.example.fluej.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<Activity>)

    @Update
    suspend fun updateActivity(activity: Activity)

    @Query("SELECT * FROM atividades WHERE data BETWEEN :inicio AND :fim")
    suspend fun getPorPeriodo(inicio: String, fim: String): List<Activity>

    @Query("SELECT * FROM atividades WHERE isSynced = 0")
    suspend fun getNaoSincronizadas(): List<Activity>
}