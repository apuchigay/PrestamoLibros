package com.example.prestamolibros.Repository

import com.example.prestamolibros.DAO.LoanDao
import com.example.prestamolibros.model.Loan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoanRepository(private val loanDao: LoanDao) {

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
}