package com.example.prestamolibros.DAO

import androidx.room.*
import com.example.prestamolibros.model.Loan
import com.example.prestamolibros.model.Book
import com.example.prestamolibros.model.Member

@Dao
interface LoanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: Loan)

    @Update
    suspend fun updateLoan(loan: Loan)

    @Delete
    suspend fun deleteLoan(loan: Loan)

    @Query("SELECT * FROM prestamos")
    suspend fun getAllLoans(): List<Loan>

    @Query("SELECT * FROM prestamos WHERE prestamoId = :id")
    suspend fun getLoanById(id: Int): Loan?

    @Query("""
        SELECT * FROM prestamos 
        WHERE libroId = :bookId 
        AND miembroId = :memberId
    """)
    suspend fun getLoanByBookAndMember(bookId: Int, memberId: Int): Loan?

    // Nuevas consultas para obtener la lista de libros y miembros
    @Query("SELECT * FROM libros")
    suspend fun getAllBooks(): List<Book>

    @Query("SELECT * FROM miembros")
    suspend fun getAllMembers(): List<Member>
}
