package com.example.prestamolibros.DAO

import androidx.room.*
import com.example.prestamolibros.model.Member

@Dao
interface MemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member)

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("SELECT * FROM miembros")
    suspend fun getAllMembers(): List<Member>

    @Query("SELECT * FROM miembros WHERE miembroId = :id")
    suspend fun getMemberById(id: Int): Member?
}