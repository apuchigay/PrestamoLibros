package com.example.prestamolibros.DAO

import androidx.room.*
import com.example.prestamolibros.model.Book

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM libros")
    suspend fun getAllBooks(): List<Book>

    @Query("SELECT * FROM libros WHERE libroId = :id")
    suspend fun getBookById(id: Int): Book?
}