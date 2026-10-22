package com.example.fluej.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<Project>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVinculos(vinculos: List<ProjectMember>)

    @Update
    suspend fun updateProject(project: Project)

    @Update
    suspend fun updateVinculo(vinculo: ProjectMember)

    @Query("SELECT * FROM projetos WHERE isSynced = 0")
    suspend fun getProjetosNaoSincronizados(): List<Project>

    @Query("SELECT * FROM projeto_membros WHERE isSynced = 0")
    suspend fun getVinculosNaoSincronizados(): List<ProjectMember>

    @Query("SELECT COUNT(*) FROM projetos WHERE setor = :setor")
    suspend fun contarPorSetor(setor: String): Int
}