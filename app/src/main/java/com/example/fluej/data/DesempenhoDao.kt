package com.example.fluej.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DesempenhoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(registros: List<DesempenhoMensal>)

    @Query("SELECT * FROM desempenho_mensal ORDER BY ordem")
    suspend fun findAll(): List<DesempenhoMensal>

    @Query("SELECT COUNT(*) FROM desempenho_mensal")
    suspend fun count(): Int
}