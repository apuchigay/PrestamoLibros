package com.example.prestamolibros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.prestamolibros.Repository.AuthorRepository
import com.example.prestamolibros.Screen.AuthorViewModel
import com.example.prestamolibros.Screen.AuthorViewModelFactory
import com.example.prestamolibros.Screen.Navigation
import com.example.prestamolibros.database.LoanSystemDatabase

class MainActivity : ComponentActivity() {
    private lateinit var authorViewModel: AuthorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener la base de datos y el DAO
        val database = LoanSystemDatabase.getDatabase(applicationContext)
        val authorDao = database.authorDao()

        // Crear el repositorio
        val authorRepository = AuthorRepository(authorDao)

        // Crear el ViewModel usando la fábrica
        val factory = AuthorViewModelFactory(authorRepository)
        authorViewModel = ViewModelProvider(this, factory)[AuthorViewModel::class.java]

        // Configurar la pantalla
        setContent {
            Navigation()
        }
    }
}