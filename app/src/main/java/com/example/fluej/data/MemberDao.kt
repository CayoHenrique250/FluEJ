package com.example.fluej.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<Member>)

    @Update
    suspend fun updateMember(member: Member)

    @Query("SELECT * FROM members")
    suspend fun getAllMembers(): List<Member>

    @Query("SELECT * FROM members WHERE isSynced = 0")
    suspend fun getMembrosNaoSincronizados(): List<Member>
}