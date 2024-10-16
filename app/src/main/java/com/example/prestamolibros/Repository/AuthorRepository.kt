package com.example.prestamolibros.Repository

import com.example.prestamolibros.DAO.AuthorDao
import com.example.prestamolibros.model.Author
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthorRepository(private val authorDao: AuthorDao) {

    suspend fun insertAuthor(author: Author) {
        withContext(Dispatchers.IO) {
            authorDao.insertAuthor(author)
        }
    }

    suspend fun updateAuthor(author: Author) {
        withContext(Dispatchers.IO) {
            authorDao.updateAuthor(author)
        }
    }

    suspend fun deleteAuthor(author: Author) {
        withContext(Dispatchers.IO) {
            authorDao.deleteAuthor(author)
        }
    }

    suspend fun getAllAuthors(): List<Author> {
        return withContext(Dispatchers.IO) {
            authorDao.getAllAuthors()
        }
    }

    suspend fun getAuthorById(id: Int): Author? {
        return withContext(Dispatchers.IO) {
            authorDao.getAuthorById(id)
        }
    }
}