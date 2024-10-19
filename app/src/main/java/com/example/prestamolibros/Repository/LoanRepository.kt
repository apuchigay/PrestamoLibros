package com.example.prestamolibros.Repository

import com.example.prestamolibros.DAO.LoanDao
import com.example.prestamolibros.DAO.BookDao // Asegúrate de tener este import si usas BookDao
import com.example.prestamolibros.DAO.MemberDao // Asegúrate de tener este import si usas MemberDao
import com.example.prestamolibros.model.Loan
import com.example.prestamolibros.model.Book
import com.example.prestamolibros.model.Member
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoanRepository(
    private val loanDao: LoanDao,
    private val bookDao: BookDao,
    private val memberDao: MemberDao
) {

    suspend fun insertLoan(loan: Loan) {
        withContext(Dispatchers.IO) {
            loanDao.insertLoan(loan)
        }
    }

    suspend fun updateLoan(loan: Loan) {
        withContext(Dispatchers.IO) {
            loanDao.updateLoan(loan)
        }
    }

    suspend fun deleteLoan(loan: Loan) {
        withContext(Dispatchers.IO) {
            loanDao.deleteLoan(loan)
        }
    }

    suspend fun getAllLoans(): List<Loan> {
        return withContext(Dispatchers.IO) {
            loanDao.getAllLoans()
        }
    }

    suspend fun getLoanById(id: Int): Loan? {
        return withContext(Dispatchers.IO) {
            loanDao.getLoanById(id)
        }
    }

    // Añadir función para obtener todos los libros
    suspend fun getAllBooks(): List<Book> {
        return withContext(Dispatchers.IO) {
            bookDao.getAllBooks()
        }
    }

    // Añadir función para obtener todos los miembros
    suspend fun getAllMembers(): List<Member> {
        return withContext(Dispatchers.IO) {
            memberDao.getAllMembers()
        }
    }
}
