package com.example.prestamolibros.DAO

import Author
import androidx.room.*

@Dao
interface AuthorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: Author)

    @Update
    suspend fun updateAuthor(author: Author)

    @Delete
    suspend fun deleteAuthor(author: Author)

    @Query("SELECT * FROM autores")
    suspend fun getAllAuthors(): List<Author>

    @Query("SELECT * FROM autores WHERE autorId = :id")
    suspend fun getAuthorById(id: Int): Author?
}