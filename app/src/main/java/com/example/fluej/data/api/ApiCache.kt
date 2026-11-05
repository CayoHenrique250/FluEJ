package com.example.fluej.data.api

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "api_cache")
data class ApiCache(
    @PrimaryKey val chave: String,
    val json: String,
    val atualizadoEm: Long
)

@Dao
interface ApiCacheDao {
    @Query("SELECT * FROM api_cache WHERE chave = :chave")
    suspend fun buscar(chave: String): ApiCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(item: ApiCache)

    @Query("DELETE FROM api_cache")
    suspend fun limpar()
}