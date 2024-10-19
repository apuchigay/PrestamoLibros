package com.example.prestamolibros.Repository

import com.example.prestamolibros.DAO.BookDao
import com.example.prestamolibros.model.Author
import com.example.prestamolibros.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookRepository(private val bookDao: BookDao) {

    suspend fun insertBook(book: Book) {
        withContext(Dispatchers.IO) {
            bookDao.insertBook(book)
        }
    }

    suspend fun updateBook(book: Book) {
        withContext(Dispatchers.IO) {
            bookDao.updateBook(book)
        }
    }

    suspend fun deleteBook(book: Book) {
        withContext(Dispatchers.IO) {
            bookDao.deleteBook(book)
        }
    }

    suspend fun getAllBooks(): List<Book> {
        return withContext(Dispatchers.IO) {
            bookDao.getAllBooks()
        }
    }

    suspend fun getBookById(id: Int): Book? {
        return withContext(Dispatchers.IO) {
            bookDao.getBookById(id)
        }
    }

    suspend fun getAllAuthors(): List<Author> {
        return withContext(Dispatchers.IO) {
            bookDao.getAllAuthors()
        }
    }
}